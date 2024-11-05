package com.workshop.vehicle.vehicle_service.application.controller;

import com.workshop.vehicle.vehicle_service.application.response.service.VehicleResponseService;
import com.workshop.vehicle.vehicle_service.application.service.VehicleCommandService;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VehicleCommandControllerTest {

    @InjectMocks
    private VehicleCommandController vehicleCommandController;

    @Mock
    private VehicleCommandService vehicleCommandService;

    @Mock
    private VehicleResponseService vehicleResponseService;

    private Vehicle vehicle;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicle = Vehicle.builder()
                .vehicleId(ObjectId.get())
                .licensePlate("ABC123")
                .capacity(5)
                .currentStatus(null)
                .type(null)
                .driver(null)
                .maintenanceDetails(null)
                .currentLocation(null)
                .lastMaintenance(null)
                .routeId(ObjectId.get())
                .build();
    }

    @Test
    public void testCreateVehicle() {
        when(vehicleCommandService.createVehicle(any(Vehicle.class))).thenReturn(Mono.just(vehicle));
        when(vehicleResponseService.buildOkResponse(any(Vehicle.class))).thenReturn(Mono.just(ResponseEntity.ok(vehicle)));

        Mono<ResponseEntity<Vehicle>> response = vehicleCommandController.createVehicle(vehicle);

        assertEquals(ResponseEntity.ok(vehicle), response.block());
        verify(vehicleCommandService, times(1)).createVehicle(any(Vehicle.class));
    }

    @Test
    public void testUpdateVehicle() {
        String idString = vehicle.getVehicleId().toHexString();
        when(vehicleCommandService.updateVehicle(any(ObjectId.class), any(Vehicle.class))).thenReturn(Mono.just(vehicle));
        when(vehicleResponseService.buildOkResponse(any(Vehicle.class))).thenReturn(Mono.just(ResponseEntity.ok(vehicle)));

        Mono<ResponseEntity<Vehicle>> response = vehicleCommandController.updateVehicle(idString, vehicle);

        assertEquals(ResponseEntity.ok(vehicle), response.block());
        verify(vehicleCommandService, times(1)).updateVehicle(any(ObjectId.class), any(Vehicle.class));
    }

    @Test
    public void testDeleteVehicle() {
        String idString = vehicle.getVehicleId().toHexString();
        when(vehicleCommandService.deleteVehicle(any(ObjectId.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> response = vehicleCommandController.deleteVehicle(idString);

        assertEquals(ResponseEntity.noContent().build(), response.block());
        verify(vehicleCommandService, times(1)).deleteVehicle(any(ObjectId.class));
    }
}