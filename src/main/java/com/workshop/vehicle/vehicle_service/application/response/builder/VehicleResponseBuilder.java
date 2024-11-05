package com.workshop.vehicle.vehicle_service.application.response.builder;

import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.springframework.http.ResponseEntity;

public class VehicleResponseBuilder {

    public static ResponseEntity<Vehicle> generateOkResponse(Vehicle vehicle) {
        return ResponseEntity.ok(vehicle);
    }

}
