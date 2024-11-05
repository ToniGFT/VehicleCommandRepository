package com.workshop.vehicle.vehicle_service.application.response.builder;

import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleResponseBuilderTest {

    @Test
    void generateOkResponse_ShouldReturnOkResponseWithVehicle() {
        Vehicle vehicle = new Vehicle();

        ResponseEntity<Vehicle> responseEntity = VehicleResponseBuilder.generateOkResponse(vehicle);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(200);
        assertThat(responseEntity.getBody()).isEqualTo(vehicle);
    }
}
