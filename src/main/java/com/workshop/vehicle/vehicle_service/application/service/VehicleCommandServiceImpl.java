package com.workshop.vehicle.vehicle_service.application.service;


import com.workshop.vehicle.vehicle_service.domain.exceptions.VehicleNotFoundException;
import com.workshop.vehicle.vehicle_service.domain.model.aggregates.Vehicle;
import com.workshop.vehicle.vehicle_service.domain.repository.VehicleRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Mono<Vehicle> createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Mono<Vehicle> updateVehicle(ObjectId id, Vehicle vehicle) {
        return vehicleRepository.findById(id)
                .switchIfEmpty(Mono.error(new VehicleNotFoundException("Vehicle not found with id: " + id)))
                .flatMap(existingVehicle -> {
                    existingVehicle.setLicensePlate(vehicle.getLicensePlate());
                    existingVehicle.setCapacity(vehicle.getCapacity());
                    existingVehicle.setCurrentStatus(vehicle.getCurrentStatus());
                    existingVehicle.setType(vehicle.getType());
                    existingVehicle.setDriver(vehicle.getDriver());
                    existingVehicle.setMaintenanceDetails(vehicle.getMaintenanceDetails());
                    existingVehicle.setCurrentLocation(vehicle.getCurrentLocation());
                    existingVehicle.setLastMaintenance(vehicle.getLastMaintenance());
                    existingVehicle.setRouteId(vehicle.getRouteId());

                    return vehicleRepository.save(existingVehicle);
                });
    }

    @Override
    public Mono<Void> deleteVehicle(ObjectId id) {
        return vehicleRepository.findById(id)
                .switchIfEmpty(Mono.error(new VehicleNotFoundException("Vehicle not found with id: " + id)))
                .flatMap(vehicleRepository::delete);
    }
}