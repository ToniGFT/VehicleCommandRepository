package com.workshop.vehicle.vehicle_service.domain.update;
import static org.mockito.Mockito.*;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.update.VehicleUpdater;
import com.workshop.vehicle.vehicle_service.domain.model.validation.VehicleValidator;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("RouteUpdater Unit Tests")
class VehicleUpdaterTest {

    @Mock
    private VehicleValidator routeValidator;

    @InjectMocks
    private VehicleUpdater routeUpdater;

    private Vehicle source;
    private Vehicle target;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ObjectId sourceId = new ObjectId("507f1f77bcf86cd799439011");
        ObjectId targetId = new ObjectId("507f191e810c19729de860ea");

        source = Vehicle.builder()
                .routeId(sourceId)
                .build();

        target = Vehicle.builder()
                .routeId(targetId)
                .build();
    }

    @Test
    @DisplayName("Map and Validate - Should Copy Data from Source to Target and Validate Target")
    void mapAndValidate_shouldCopyDataAndValidateTarget() {
        // Arrange
        doNothing().when(routeValidator).validate(target);

        // Act
        Mono<Vehicle> result = routeUpdater.mapAndValidate(source, target);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(updatedRoute ->
                        updatedRoute.getRouteId().equals(target.getRouteId()))
                .verifyComplete();

        verify(routeValidator, times(1)).validate(target);
    }

    @Test
    @DisplayName("Map and Validate - Should Throw Validation Exception if Target is Invalid")
    void mapAndValidate_shouldThrowValidationExceptionIfTargetInvalid() {
        // Arrange
        doThrow(new IllegalArgumentException("Validation error")).when(routeValidator).validate(target);

        // Act & Assert
        StepVerifier.create(routeUpdater.mapAndValidate(source, target))
                .expectErrorMatches(error -> error instanceof RuntimeException)
                .verify();

        verify(routeValidator, times(1)).validate(target);
    }


}