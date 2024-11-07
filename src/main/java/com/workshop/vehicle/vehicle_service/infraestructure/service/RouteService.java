package com.workshop.vehicle.vehicle_service.infraestructure.service;

import com.workshop.vehicle.vehicle_service.infraestructure.models.aggregates.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RouteService {

    private final WebClient webClient;
    private String baseUrl;
    private String routeUrl;

    @Autowired
    public RouteService(WebClient webClient,
                        @Value("${routes.api.base-url}") String baseUrl,
                        @Value("${routes.api.get-by-id}") String routeUrl) {
        this.webClient = webClient;
        this.routeUrl=routeUrl;
        this.baseUrl=baseUrl;
    }

    public Mono<Route> getRouteById(String idString) {
        return webClient.get()
                .uri(baseUrl + routeUrl, idString)
                .retrieve()
                .bodyToMono(Route.class)
                .onErrorResume(error -> {
                    System.err.println("Error al llamar al servicio de rutas: " + error.getMessage());
                    return Mono.empty();
                });
    }
}
