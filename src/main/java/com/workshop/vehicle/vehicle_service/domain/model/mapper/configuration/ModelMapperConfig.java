package com.workshop.vehicle.vehicle_service.domain.model.mapper.configuration;

import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;

public class ModelMapperConfig {

    public static ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        configureRouteMapping(modelMapper);

        return modelMapper;
    }

    private static void configureRouteMapping(ModelMapper modelMapper) {
        modelMapper.typeMap(Vehicle.class, Vehicle.class).addMappings(mapper -> {
            mapper.skip(Vehicle::setRouteId);
        });
    }
}
