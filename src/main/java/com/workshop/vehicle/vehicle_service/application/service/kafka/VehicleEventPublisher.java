package com.workshop.vehicle.vehicle_service.application.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.vehicle.vehicle_service.domain.events.VehicleCreatedEvent;
import com.workshop.vehicle.vehicle_service.domain.events.VehicleDeletedEvent;
import com.workshop.vehicle.vehicle_service.domain.events.VehicleUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.HashMap;
import java.util.Map;

@Service
public class VehicleEventPublisher {

    private final KafkaSender<String, String> kafkaSender;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.vehicle-events}")
    private String vehicleEventsTopic;

    public VehicleEventPublisher(KafkaSender<String, String> kafkaSender, ObjectMapper objectMapper) {
        this.kafkaSender = kafkaSender;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> publishVehicleCreatedEvent(VehicleCreatedEvent event) {
        return publishEvent("VEHICLE_CREATED", event);
    }

    public Mono<Void> publishVehicleUpdatedEvent(VehicleUpdatedEvent event) {
        return publishEvent("VEHICLE_UPDATED", event);
    }

    public Mono<Void> publishVehicleDeletedEvent(VehicleDeletedEvent event) {
        return publishEvent("VEHICLE_DELETED", event);
    }

    private <T> Mono<Void> publishEvent(String eventType, T event) {
        try {
            Map<String, Object> messagePayload = new HashMap<>();
            messagePayload.put("type", eventType);
            messagePayload.putAll(objectMapper.convertValue(event, Map.class));

            String message = objectMapper.writeValueAsString(messagePayload);
            return kafkaSender.send(Mono.just(
                    SenderRecord.create(vehicleEventsTopic, null, System.currentTimeMillis(), null, message, null)
            )).then();
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to publish event: " + eventType, e));
        }
    }
}
