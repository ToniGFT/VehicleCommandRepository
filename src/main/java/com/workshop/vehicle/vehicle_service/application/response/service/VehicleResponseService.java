package com.workshop.vehicle.vehicle_service.application.response.service;

import com.workshop.vehicle.vehicle_service.application.response.builder.VehicleResponseBuilder;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class VehicleResponseService {

    public Mono<ResponseEntity<Vehicle>> buildOkResponse(Vehicle vehicle) {
        return Mono.fromCallable(() -> VehicleResponseBuilder.generateOkResponse(vehicle));
    }

    public Flux<ResponseEntity<Vehicle>> buildVehiclesResponse(Flux<Vehicle> routes) {
        return routes.map(VehicleResponseBuilder::generateOkResponse);
    }
}
