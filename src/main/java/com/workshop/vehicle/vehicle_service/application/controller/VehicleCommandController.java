package com.workshop.vehicle.vehicle_service.application.controller;

import com.workshop.vehicle.vehicle_service.application.response.service.VehicleResponseService;
import com.workshop.vehicle.vehicle_service.application.service.VehicleCommandService;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/vehicles")
public class VehicleCommandController {

    private final VehicleCommandService vehicleCommandService;
    private final VehicleResponseService vehicleResponseService;

    @Autowired
    public VehicleCommandController(VehicleCommandService vehicleCommandService,
                                    VehicleResponseService vehicleResponseService) {
        this.vehicleCommandService = vehicleCommandService;
        this.vehicleResponseService = vehicleResponseService;
    }

    @PostMapping
    public Mono<ResponseEntity<Vehicle>> createVehicle(@RequestBody Vehicle vehicle) {
        return vehicleCommandService.createVehicle(vehicle)
                .flatMap(vehicleResponseService::buildOkResponse);
    }

    @PutMapping("/{idString}")
    public Mono<ResponseEntity<Vehicle>> updateVehicle(@PathVariable String idString, @RequestBody Vehicle vehicle) {
        ObjectId id = new ObjectId(idString);
        return vehicleCommandService.updateVehicle(id, vehicle)
                .flatMap(vehicleResponseService::buildOkResponse);
    }

    @DeleteMapping("/{idString}")
    public Mono<ResponseEntity<Void>> deleteVehicle(@PathVariable String idString) {
        ObjectId id = new ObjectId(idString);
        return vehicleCommandService.deleteVehicle(id)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }
}
