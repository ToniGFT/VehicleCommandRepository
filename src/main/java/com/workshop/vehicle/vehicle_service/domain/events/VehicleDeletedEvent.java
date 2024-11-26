package com.workshop.vehicle.vehicle_service.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDeletedEvent {
    private ObjectId vehicleId;
}
