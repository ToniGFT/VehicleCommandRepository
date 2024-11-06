package com.workshop.vehicle.vehicle_service.application.controller;

import com.workshop.vehicle.vehicle_service.application.service.VehicleCommandService;
import com.workshop.vehicle.vehicle_service.application.response.service.VehicleResponseService;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.entities.Driver;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.Contact;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.Coordinates;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.MaintenanceDetails;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.enums.VehicleStatus;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.enums.VehicleType;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = VehicleCommandController.class)
public class VehicleCommandControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VehicleCommandService vehicleCommandService;

    @MockBean
    private VehicleResponseService vehicleResponseService;

    private Vehicle testVehicle;

    @BeforeEach
    public void setUp() {
        testVehicle = Vehicle.builder()
                .vehicleId(new ObjectId("672b1343bb9d3b2fdd48ac14"))
                .licensePlate("XYZ123")
                .capacity(50)
                .currentStatus(VehicleStatus.IN_SERVICE)
                .type(VehicleType.BUS)
                .driver(Driver.builder()
                        .name("John Doe")
                        .contact(Contact.builder()
                                .email("john.doe@example.com")
                                .phone("+123456789")
                                .build())
                        .build())
                .currentLocation(Coordinates.builder()
                        .latitude(40.7128)
                        .longitude(-74.0060)
                        .build())
                .maintenanceDetails(MaintenanceDetails.builder()
                        .scheduledBy("Admin")
                        .scheduledDate(LocalDate.now())
                        .details("Routine check")
                        .build())
                .routeId(new ObjectId("672b1343bb9d3b2fdd48ac15"))
                .build();
    }

    @Test
    public void testDeleteVehicle() {
        when(vehicleCommandService.deleteVehicle(eq(testVehicle.getVehicleId())))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/vehicles/{idString}", testVehicle.getVehicleId().toHexString())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testCreateVehicle() {
        Vehicle newVehicle = new Vehicle();
        newVehicle.setLicensePlate("ABC123");
        newVehicle.setCapacity(40);

        when(vehicleCommandService.createVehicle(any(Vehicle.class)))
                .thenReturn(Mono.just(newVehicle));

        when(vehicleResponseService.buildOkResponse(any(Vehicle.class)))
                .thenReturn(Mono.just(ResponseEntity.ok(newVehicle)));

        webTestClient.post()
                .uri("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newVehicle)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.licensePlate").isEqualTo("ABC123")
                .jsonPath("$.capacity").isEqualTo(40);
    }

    @Test
    public void testUpdateVehicle() {
        Vehicle updatedVehicle = Vehicle.builder()
                .vehicleId(testVehicle.getVehicleId())
                .licensePlate("XYZ123-Updated")
                .capacity(60)
                .currentStatus(VehicleStatus.OUT_OF_SERVICE)
                .type(VehicleType.BUS)
                .build();

        when(vehicleCommandService.updateVehicle(eq(testVehicle.getVehicleId()), any(Vehicle.class)))
                .thenReturn(Mono.just(updatedVehicle));

        when(vehicleResponseService.buildOkResponse(any(Vehicle.class)))
                .thenReturn(Mono.just(ResponseEntity.ok(updatedVehicle)));

        webTestClient.put()
                .uri("/vehicles/{idString}", testVehicle.getVehicleId().toHexString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedVehicle)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.licensePlate").isEqualTo("XYZ123-Updated")
                .jsonPath("$.capacity").isEqualTo(60)
                .jsonPath("$.currentStatus").isEqualTo("OUT_OF_SERVICE");
    }

    @Test
    public void testUpdateVehicleNotFound() {
        ObjectId nonExistentId = new ObjectId();
        Vehicle updatedVehicle = Vehicle.builder()
                .vehicleId(nonExistentId)
                .licensePlate("XYZ999")
                .capacity(40)
                .currentStatus(VehicleStatus.OUT_OF_SERVICE)
                .type(VehicleType.BUS)
                .build();

        when(vehicleCommandService.updateVehicle(eq(nonExistentId), any(Vehicle.class)))
                .thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/vehicles/{idString}", nonExistentId.toHexString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedVehicle)
                .exchange()
                .expectStatus().isNotFound();
    }

}
