package com.workshop.vehicle.vehicle_service.application.service;

import com.workshop.vehicle.vehicle_service.application.service.kafka.VehicleEventPublisher;
import com.workshop.vehicle.vehicle_service.domain.events.VehicleDeletedEvent;
import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleNotFoundException;
import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleUpdateException;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.mapper.VehicleMapper;
import com.workshop.vehicle.vehicle_service.domain.model.update.VehicleUpdater;
import com.workshop.vehicle.vehicle_service.domain.repository.VehicleRepository;
import com.workshop.vehicle.vehicle_service.infraestructure.service.RouteService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleCommandServiceImpl.class);

    private final VehicleRepository vehicleRepository;
    private final VehicleUpdater vehicleUpdater;
    private final VehicleEventPublisher eventPublisher;
    private final RouteService routeService;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository,
                                     VehicleUpdater vehicleUpdater,
                                     VehicleEventPublisher eventPublisher,
                                     RouteService routeService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleUpdater = vehicleUpdater;
        this.eventPublisher = eventPublisher;
        this.routeService = routeService;
    }

    @Override
    public Mono<Vehicle> createVehicle(Vehicle vehicle) {
        return routeService.getRouteById(vehicle.getRouteId().toHexString())
                .flatMap(route -> Mono.just(vehicle))
                .flatMap(vehicleRepository::save)
                .doOnSuccess(savedVehicle -> logger.info("Vehicle saved successfully in database: {}", savedVehicle))
                .flatMap(savedVehicle -> eventPublisher
                        .publishVehicleCreatedEvent(VehicleMapper.toVehicleCreatedEvent(savedVehicle))
                        .doOnSuccess(v -> logger.info("VehicleCreatedEvent published successfully"))
                        .thenReturn(savedVehicle))
                .onErrorMap(e -> {
                    logger.error("Error occurred during vehicle creation: {}", e.getMessage());
                    return e;
                });
    }

    @Override
    public Mono<Vehicle> updateVehicle(ObjectId id, Vehicle vehicle) {
        return vehicleRepository.findById(id)
                .switchIfEmpty(Mono.error(new VehicleNotFoundException("Vehicle not found with id: " + id)))
                .flatMap(existingVehicle -> vehicleUpdater.mapAndValidate(vehicle, existingVehicle)
                        .onErrorMap(e -> new VehicleUpdateException("Failed to update vehicle: " + e.getMessage())))
                .flatMap(vehicleRepository::save)
                .doOnSuccess(updatedVehicle -> logger.info("Vehicle updated successfully: {}", updatedVehicle))
                .flatMap(updatedVehicle -> eventPublisher
                        .publishVehicleUpdatedEvent(VehicleMapper.toVehicleUpdatedEvent(updatedVehicle))
                        .thenReturn(updatedVehicle));
    }

    @Override
    public Mono<Void> deleteVehicle(ObjectId id) {
        return vehicleRepository.findById(id)
                .switchIfEmpty(Mono.error(new VehicleNotFoundException("Vehicle not found with id: " + id)))
                .flatMap(existingVehicle -> vehicleRepository.deleteById(id)
                        .then(eventPublisher.publishVehicleDeletedEvent(
                                VehicleDeletedEvent.builder()
                                        .vehicleId(existingVehicle.getVehicleId())
                                        .build()
                        )));
    }
}
