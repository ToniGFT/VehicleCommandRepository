package com.workshop.vehicle.vehicle_service.application.service;

import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.bson.types.ObjectId;
import reactor.core.publisher.Mono;

public interface VehicleCommandService {
    Mono<Vehicle> createVehicle(Vehicle vehicle);
    Mono<Vehicle> updateVehicle(ObjectId id, Vehicle vehicle);
    Mono<Void> deleteVehicle(ObjectId id);
}
