package com.workshop.vehicle.vehicle_service.application.service;

import com.workshop.vehicle.vehicle_service.application.service.VehicleCommandServiceImpl;
import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleNotFoundException;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.update.VehicleUpdater;
import com.workshop.vehicle.vehicle_service.domain.repository.VehicleRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VehicleCommandServiceImplTest {

    @InjectMocks
    private VehicleCommandServiceImpl vehicleCommandService;

    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private VehicleUpdater vehicleUpdater;

    private Vehicle vehicle;
    private ObjectId vehicleId;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicleId = ObjectId.get();
        vehicle = Vehicle.builder()
                .vehicleId(vehicleId)
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
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(Mono.just(vehicle));

        Mono<Vehicle> response = vehicleCommandService.createVehicle(vehicle);

        assertEquals(vehicle, response.block());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    public void testUpdateVehicleSuccess() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Mono.just(vehicle));
        when(vehicleUpdater.mapAndValidate(any(Vehicle.class), any(Vehicle.class))).thenReturn(Mono.just(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(Mono.just(vehicle));

        Mono<Vehicle> response = vehicleCommandService.updateVehicle(vehicleId, vehicle);

        assertEquals(vehicle, response.block());
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleUpdater, times(1)).mapAndValidate(any(Vehicle.class), any(Vehicle.class));
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }


    @Test
    public void testUpdateVehicleNotFound() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Mono.empty());

        VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class, () -> {
            vehicleCommandService.updateVehicle(vehicleId, vehicle).block();
        });

        assertEquals("Vehicle not found with id: " + vehicleId, exception.getMessage());
        verify(vehicleRepository, times(1)).findById(vehicleId);
    }

    @Test
    public void testDeleteVehicleSuccess() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Mono.just(vehicle));
        when(vehicleRepository.delete(any(Vehicle.class))).thenReturn(Mono.empty());

        Mono<Void> response = vehicleCommandService.deleteVehicle(vehicleId);

        response.block();
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, times(1)).delete(any(Vehicle.class));
    }

    @Test
    public void testDeleteVehicleNotFound() {
        when(vehicleRepository.findById(vehicleId)).thenReturn(Mono.empty());

        VehicleNotFoundException exception = assertThrows(VehicleNotFoundException.class, () -> {
            vehicleCommandService.deleteVehicle(vehicleId).block();
        });

        assertEquals("Vehicle not found with id: " + vehicleId, exception.getMessage());
        verify(vehicleRepository, times(1)).findById(vehicleId);
    }
}