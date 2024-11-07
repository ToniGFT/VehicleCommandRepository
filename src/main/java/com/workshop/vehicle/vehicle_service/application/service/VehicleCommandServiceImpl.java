package com.workshop.vehicle.vehicle_service.application.service;


import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleNotFoundException;
import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleUpdateException;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.update.VehicleUpdater;
import com.workshop.vehicle.vehicle_service.domain.model.validation.VehicleValidator;
import com.workshop.vehicle.vehicle_service.domain.repository.VehicleRepository;
import com.workshop.vehicle.vehicle_service.infraestructure.service.RouteService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository vehicleRepository;
    private final VehicleUpdater vehicleUpdater;
    private final RouteService routeService;


    @Autowired
    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository,VehicleUpdater vehicleUpdater, RouteService routeService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleUpdater = vehicleUpdater;
        this.routeService = routeService;
    }

    public Mono<Vehicle> createVehicle(Vehicle vehicle) {
        return routeService.getRouteById(vehicle.getRouteId().toHexString())
                .flatMap(route -> {
                    return vehicleRepository.save(vehicle);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Route not found with id: " + vehicle.getRouteId().toString())));
    }

    @Override
    public Mono<Vehicle> updateVehicle(ObjectId id, Vehicle vehicle) {
        return vehicleRepository.findById(id)
                .switchIfEmpty(Mono.error(new VehicleNotFoundException("Vehicle not found with id: " + id)))
                .flatMap(existingRoute -> vehicleUpdater.mapAndValidate(vehicle, existingRoute)
                        .onErrorMap(e -> new VehicleUpdateException("Failed to update route: " + e.getMessage())))
                .flatMap(vehicleRepository::save);
    }

    @Override
    public Mono<Void> deleteVehicle(ObjectId id) {
        return vehicleRepository.findById(id)
                .switchIfEmpty(Mono.error(new VehicleNotFoundException("Vehicle not found with id: " + id)))
                .flatMap(vehicleRepository::delete);
    }
}