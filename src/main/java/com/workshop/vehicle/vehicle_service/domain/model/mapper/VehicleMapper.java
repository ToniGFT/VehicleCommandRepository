package com.workshop.vehicle.vehicle_service.domain.model.mapper;

import com.workshop.vehicle.vehicle_service.domain.events.VehicleCreatedEvent;
import com.workshop.vehicle.vehicle_service.domain.events.VehicleDeletedEvent;
import com.workshop.vehicle.vehicle_service.domain.events.VehicleUpdatedEvent;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.model.mapper.configuration.ModelMapperConfig;
import org.modelmapper.ModelMapper;

public class VehicleMapper {

    private static final ModelMapper modelMapper = ModelMapperConfig.getModelMapper();

    private VehicleMapper() {
    }

    public static void mapRouteData(Vehicle source, Vehicle destination) {
        modelMapper.map(source, destination);
    }

    public static VehicleCreatedEvent toVehicleCreatedEvent(Vehicle vehicle) {
        return modelMapper.map(vehicle, VehicleCreatedEvent.class);
    }

    public static VehicleUpdatedEvent toVehicleUpdatedEvent(Vehicle vehicle) {
        return modelMapper.map(vehicle, VehicleUpdatedEvent.class);
    }

    public static VehicleDeletedEvent toVehicleDeletedEvent(Vehicle vehicle) {
        return VehicleDeletedEvent.builder()
                .vehicleId(vehicle.getVehicleId())
                .build();
    }
}
