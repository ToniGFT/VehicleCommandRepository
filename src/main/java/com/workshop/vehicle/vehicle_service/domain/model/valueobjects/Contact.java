package com.workshop.vehicle.vehicle_service.domain.model.valueobjects;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    @Email(message = "Email must be in a valid format")
    private String email;

    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Phone number must be in a valid format")
    private String phone;
}
