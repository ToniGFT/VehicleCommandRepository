package com.workshop.vehicle.vehicle_service.application.service;


import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleNotFoundException;
import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleUpdateException;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.update.VehicleUpdater;
import com.workshop.vehicle.vehicle_service.domain.model.validation.VehicleValidator;
import com.workshop.vehicle.vehicle_service.domain.repository.VehicleRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository vehicleRepository;
    private final VehicleValidator vehicleValidator;
    private final VehicleUpdater vehicleUpdater;

    @Autowired
    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository, VehicleValidator vehicleValidator, VehicleUpdater vehicleUpdater) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleValidator = vehicleValidator;
        this.vehicleUpdater = vehicleUpdater;

    }

    @Override
    public Mono<Vehicle> createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
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