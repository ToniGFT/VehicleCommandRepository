package com.workshop.vehicle.vehicle_service.domain.exceptions;

public class VehicleServiceException extends RuntimeException {
    public VehicleServiceException(String message) {
        super(message);
    }
}
