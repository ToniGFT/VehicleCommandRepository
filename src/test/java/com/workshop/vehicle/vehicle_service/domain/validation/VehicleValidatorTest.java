package com.workshop.vehicle.vehicle_service.domain.validation;

import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.validation.VehicleValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class VehicleValidatorTest {

    private Validator validator;
    private VehicleValidator vehicleValidator;

    @BeforeEach
    void setUp() {
        validator = Mockito.mock(Validator.class);
        vehicleValidator = new VehicleValidator(validator);
    }

    @Test
    void validate_ShouldThrowException_WhenValidationFails() {
        Vehicle vehicle = new Vehicle();
        Set<ConstraintViolation<Vehicle>> violations = new HashSet<>();

        ConstraintViolation<Vehicle> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Invalid vehicle data");
        violations.add(violation);

        when(validator.validate(vehicle)).thenReturn(violations);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            vehicleValidator.validate(vehicle);
        });

        assertTrue(exception.getMessage().contains("Validation errors: Invalid vehicle data"));
    }

    @Test
    void validate_ShouldNotThrowException_WhenValidationPasses() {
        Vehicle vehicle = new Vehicle();
        Set<ConstraintViolation<Vehicle>> violations = new HashSet<>();

        when(validator.validate(vehicle)).thenReturn(violations);

        vehicleValidator.validate(vehicle);
    }
}
