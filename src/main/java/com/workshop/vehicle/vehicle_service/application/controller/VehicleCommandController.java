package com.workshop.vehicle.vehicle_service.application.controller;

import com.workshop.vehicle.vehicle_service.application.response.service.VehicleResponseService;
import com.workshop.vehicle.vehicle_service.application.service.VehicleCommandService;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/vehicles")
public class VehicleCommandController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleCommandController.class);
    private final VehicleCommandService vehicleCommandService;
    private final VehicleResponseService vehicleResponseService;

    public VehicleCommandController(VehicleCommandService vehicleCommandService,
                                    VehicleResponseService vehicleResponseService) {
        this.vehicleCommandService = vehicleCommandService;
        this.vehicleResponseService = vehicleResponseService;
    }

    @PostMapping
    public Mono<ResponseEntity<Vehicle>> createVehicle(@RequestBody Vehicle vehicle) {
        logger.info("Creating a new vehicle");
        return vehicleCommandService.createVehicle(vehicle)
                .flatMap(vehicleResponseService::buildOkResponse)
                .doOnSuccess(response -> logger.info("Successfully created vehicle with ID: {}", response.getBody() != null ? response.getBody().getVehicleId() : "N/A"))
                .doOnError(error -> logger.error("Error occurred while creating vehicle", error));
    }

    @PutMapping("/{idString}")
    public Mono<ResponseEntity<Vehicle>> updateVehicle(@PathVariable("idString") String idString, @RequestBody Vehicle vehicle) {
        ObjectId id = new ObjectId(idString);
        logger.info("Updating vehicle with ID: {}", id);
        return vehicleCommandService.updateVehicle(id, vehicle)
                .flatMap(vehicleResponseService::buildOkResponse)
                .doOnSuccess(response -> logger.info("Successfully updated vehicle with ID: {}", id))
                .doOnError(error -> logger.error("Error occurred while updating vehicle with ID: {}", id, error))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idString}")
    public Mono<ResponseEntity<Void>> deleteVehicle(@PathVariable("idString") String idString) {
        ObjectId id = new ObjectId(idString);
        logger.info("Deleting vehicle with ID: {}", id);
        return vehicleCommandService.deleteVehicle(id)
                .then(Mono.defer(() -> {
                    logger.info("Successfully deleted vehicle with ID: {}", id);
                    return Mono.just(ResponseEntity.noContent().<Void>build());
                }))
                .doOnError(error -> logger.error("Error occurred while deleting vehicle with ID: {}", id, error));
    }

}
