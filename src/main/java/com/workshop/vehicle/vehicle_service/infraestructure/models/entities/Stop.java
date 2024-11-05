package com.workshop.vehicle.vehicle_service.infraestructure.models.entities;

import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.Coordinates;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stop {

    @NotEmpty(message = "Stop ID cannot be empty")
    private String stopId;

    @NotEmpty(message = "Stop name cannot be empty")
    private String stopName;

    @NotNull(message = "Coordinates cannot be null")
    @Valid
    private Coordinates coordinates;

    @NotNull(message = "There must be at least one arrival time")
    @NotEmpty(message = "Arrival times list cannot be empty")
    private List<String> arrivalTimes;
}
