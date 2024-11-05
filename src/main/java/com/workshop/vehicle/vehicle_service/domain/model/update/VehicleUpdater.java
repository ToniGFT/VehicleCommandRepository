package com.workshop.vehicle.vehicle_service.domain.model.update;

import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleUpdateException;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.mapper.VehicleMapper;
import com.workshop.vehicle.vehicle_service.domain.model.validation.VehicleValidator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class VehicleUpdater {

    private final VehicleValidator vehicleValidator;

    public VehicleUpdater(VehicleValidator vehicleValidator) {
        this.vehicleValidator = vehicleValidator;
    }

    public Mono<Vehicle> mapAndValidate(Vehicle source, Vehicle target) {
        VehicleMapper.mapRouteData(source, target);
        return Mono.fromRunnable(() -> vehicleValidator.validate(target))
                .thenReturn(target)
                .onErrorMap(e -> new VehicleUpdateException("Validation failed: " + e.getMessage()));
    }

}
