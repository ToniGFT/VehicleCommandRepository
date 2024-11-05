package com.workshop.vehicle.vehicle_service.application.response.service;

import com.workshop.vehicle.vehicle_service.application.response.builder.VehicleResponseBuilder;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class VehicleResponseServiceTest {

    private VehicleResponseService vehicleResponseService;

    @BeforeEach
    void setUp() {
        vehicleResponseService = new VehicleResponseService();
    }

    @Test
    void buildOkResponse_ShouldReturnOkResponse() {
        Vehicle vehicle = new Vehicle();
        ResponseEntity<Vehicle> expectedResponse = ResponseEntity.ok(vehicle);

        try (MockedStatic<VehicleResponseBuilder> mockedStatic = mockStatic(VehicleResponseBuilder.class)) {
            mockedStatic.when(() -> VehicleResponseBuilder.generateOkResponse(any(Vehicle.class)))
                    .thenReturn(expectedResponse);

            Mono<ResponseEntity<Vehicle>> responseMono = vehicleResponseService.buildOkResponse(vehicle);

            StepVerifier.create(responseMono)
                    .expectNext(expectedResponse)
                    .verifyComplete();
        }
    }

    @Test
    void buildVehiclesResponse_ShouldReturnFluxOfOkResponses() {
        Vehicle vehicle1 = new Vehicle();
        Vehicle vehicle2 = new Vehicle();
        ResponseEntity<Vehicle> response1 = ResponseEntity.ok(vehicle1);
        ResponseEntity<Vehicle> response2 = ResponseEntity.ok(vehicle2);

        try (MockedStatic<VehicleResponseBuilder> mockedStatic = mockStatic(VehicleResponseBuilder.class)) {
            mockedStatic.when(() -> VehicleResponseBuilder.generateOkResponse(vehicle1)).thenReturn(response1);
            mockedStatic.when(() -> VehicleResponseBuilder.generateOkResponse(vehicle2)).thenReturn(response2);

            Flux<Vehicle> vehicleFlux = Flux.just(vehicle1, vehicle2);

            Flux<ResponseEntity<Vehicle>> responseFlux = vehicleResponseService.buildVehiclesResponse(vehicleFlux);

            StepVerifier.create(responseFlux)
                    .expectNext(response1)
                    .expectNext(response2)
                    .verifyComplete();
        }
    }
}