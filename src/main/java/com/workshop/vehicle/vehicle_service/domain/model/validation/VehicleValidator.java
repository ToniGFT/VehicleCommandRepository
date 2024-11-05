package com.workshop.vehicle.vehicle_service.domain.model.validation;

import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class VehicleValidator {

    private final Validator validator;

    public VehicleValidator(Validator validator) {
        this.validator = validator;
    }

    public void validate(Vehicle route) {
        Set<ConstraintViolation<Vehicle>> violations = validator.validate(route);
        if (!violations.isEmpty()) {
            String errorMessages = formatValidationErrors(violations);
            throw new IllegalArgumentException("Validation errors: " + errorMessages);
        }
    }

    private String formatValidationErrors(Set<ConstraintViolation<Vehicle>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
    }
}
