package com.workshop.vehicle.vehicle_service.domain.events;

import com.workshop.vehicle.vehicle_service.domain.model.entities.Driver;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.Coordinates;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.MaintenanceDetails;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.enums.VehicleStatus;
import com.workshop.vehicle.vehicle_service.domain.model.valueobjects.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleUpdatedEvent {
    private ObjectId vehicleId;
    private String licensePlate;
    private Integer capacity;
    private VehicleStatus currentStatus;
    private VehicleType vehicleType;
    private Driver driver;
    private MaintenanceDetails maintenanceDetails;
    private Coordinates currentLocation;
    private LocalDate lastMaintenance;
    private ObjectId routeId;
}
