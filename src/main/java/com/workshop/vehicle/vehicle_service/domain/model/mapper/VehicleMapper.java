package com.workshop.vehicle.vehicle_service.domain.model.mapper;


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
}
