package com.workshop.vehicle.vehicle_service.domain.model.entities;

import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.Contact;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

    private ObjectId driverId;

    @NotBlank(message = "Driver name cannot be empty")
    private String name;

    @Valid
    private Contact contact;
}
