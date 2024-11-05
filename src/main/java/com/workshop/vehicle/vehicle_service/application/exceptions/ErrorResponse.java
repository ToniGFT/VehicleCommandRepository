package com.workshop.vehicle.vehicle_service.application.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private int code;
    private String message;
}