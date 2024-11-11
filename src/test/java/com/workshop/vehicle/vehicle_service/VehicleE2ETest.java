package com.workshop.vehicle.vehicle_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.workshop.vehicle.vehicle_service.application.controller.VehicleCommandController;
import com.workshop.vehicle.vehicle_service.application.response.service.VehicleResponseService;
import com.workshop.vehicle.vehicle_service.application.service.VehicleCommandService;
import com.workshop.vehicle.vehicle_service.application.service.VehicleCommandServiceImpl;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.entities.Driver;
import com.workshop.vehicle.vehicle_service.domain.model.update.VehicleUpdater;
import com.workshop.vehicle.vehicle_service.domain.model.validation.VehicleValidator;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.Contact;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.Coordinates;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.MaintenanceDetails;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.enums.VehicleStatus;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.enums.VehicleType;
import com.workshop.vehicle.vehicle_service.domain.repository.VehicleRepository;
import com.workshop.vehicle.vehicle_service.infraestructure.models.aggregates.Route;
import com.workshop.vehicle.vehicle_service.infraestructure.service.RouteService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = VehicleCommandController.class)
@Import({VehicleCommandServiceImpl.class, VehicleResponseService.class})
public class VehicleE2ETest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private RouteService routeService;

    @MockBean
    private VehicleUpdater vehicleUpdater;

    private Vehicle testVehicle;
    private Route testRoute;

    @BeforeEach
    public void setUp() {
        // Configurar el objeto de prueba Vehicle
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

        // Configurar el objeto de prueba Route
        testRoute = new Route();
        testRoute.setRouteId(testVehicle.getRouteId());
    }

    @Test
    public void testCreateVehicleE2E() {
        // Mock del servicio externo RouteService
        when(routeService.getRouteById(eq(testVehicle.getRouteId().toHexString())))
                .thenReturn(Mono.just(testRoute));

        // Mock del repositorio VehicleRepository
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(Mono.just(testVehicle));

        // Crear el cuerpo de la petición como un Map para evitar problemas de serialización con ObjectId
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("licensePlate", testVehicle.getLicensePlate());
        requestBody.put("capacity", testVehicle.getCapacity());
        requestBody.put("currentStatus", testVehicle.getCurrentStatus());
        requestBody.put("type", testVehicle.getType());
        requestBody.put("driver", testVehicle.getDriver());
        requestBody.put("currentLocation", testVehicle.getCurrentLocation());
        requestBody.put("maintenanceDetails", testVehicle.getMaintenanceDetails());
        requestBody.put("routeId", testVehicle.getRouteId().toHexString());

        webTestClient.post()
                .uri("/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.licensePlate").isEqualTo("XYZ123")
                .jsonPath("$.capacity").isEqualTo(50);
    }

    @Test
    public void testUpdateVehicleE2E() {
        Vehicle updatedVehicle = Vehicle.builder()
                .vehicleId(testVehicle.getVehicleId())
                .licensePlate("XYZ123-Updated")
                .capacity(60)
                .currentStatus(VehicleStatus.OUT_OF_SERVICE)
                .type(VehicleType.BUS)
                .routeId(testVehicle.getRouteId())
                .build();

        when(vehicleRepository.findById(eq(testVehicle.getVehicleId())))
                .thenReturn(Mono.just(testVehicle));

        when(vehicleUpdater.mapAndValidate(any(Vehicle.class), any(Vehicle.class)))
                .thenReturn(Mono.just(updatedVehicle));

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(Mono.just(updatedVehicle));

        // Crear el cuerpo de la petición como un Map
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("licensePlate", updatedVehicle.getLicensePlate());
        requestBody.put("capacity", updatedVehicle.getCapacity());
        requestBody.put("currentStatus", updatedVehicle.getCurrentStatus());
        requestBody.put("type", updatedVehicle.getType());
        requestBody.put("routeId", updatedVehicle.getRouteId().toHexString());

        webTestClient.put()
                .uri("/vehicles/{idString}", testVehicle.getVehicleId().toHexString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.licensePlate").isEqualTo("XYZ123-Updated")
                .jsonPath("$.capacity").isEqualTo(60)
                .jsonPath("$.currentStatus").isEqualTo("OUT_OF_SERVICE");
    }

    @Test
    public void testDeleteVehicleE2E() {
        when(vehicleRepository.findById(eq(testVehicle.getVehicleId())))
                .thenReturn(Mono.just(testVehicle));

        when(vehicleRepository.delete(eq(testVehicle)))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/vehicles/{idString}", testVehicle.getVehicleId().toHexString())
                .exchange()
                .expectStatus().isNoContent();
    }
}
