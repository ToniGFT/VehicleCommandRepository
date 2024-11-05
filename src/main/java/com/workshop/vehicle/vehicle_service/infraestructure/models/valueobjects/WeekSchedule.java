package com.workshop.vehicle.vehicle_service.infraestructure.models.valueobjects;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekSchedule {

    @NotNull(message = "Start time cannot be null")
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    private LocalTime endTime;

    @NotNull(message = "Frequency in minutes cannot be null")
    @Positive(message = "Frequency must be a positive number")
    private Integer frequencyMinutes;

    @AssertTrue(message = "End time must be after start time")
    public boolean isEndTimeAfterStartTime() {
        return endTime == null || startTime == null || endTime.isAfter(startTime);
    }
}
