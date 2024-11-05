package com.workshop.vehicle.vehicle_service.domain.repository;

import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface VehicleRepository extends ReactiveMongoRepository<Vehicle, ObjectId> {

}
