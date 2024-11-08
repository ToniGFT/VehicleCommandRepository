package com.workshop.vehicle.vehicle_service.infraestructure.service;

import com.workshop.vehicle.vehicle_service.infraestructure.models.aggregates.Route;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;


class RouteServiceTest {

    private MockWebServer mockWebServer;
    private RouteService routeService;
    private WebClient webClient;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.create(mockWebServer.url("/").toString());

        routeService = new RouteService(webClient,
                mockWebServer.url("/").toString(),
                "/routes/{id}");
    }

    @Test
    @DisplayName("When fetching a route by ID, then the correct route is returned")
    void testGetRouteById() {
        String routeJson = """
          {
            "id": "6720c504a8c6119cff20e881",
            "routeName": "Ruta Centro-Norte",
            "stops": [
              {
                "stopId": "1",
                "stopName": "Estación Central",
                "coordinates": {
                  "latitude": 40.712776,
                  "longitude": -74.005974
                },
                "arrivalTimes": [
                  "08:00",
                  "08:30",
                  "09:00"
                ]
              },
              {
                "stopId": "2",
                "stopName": "Plaza Norte",
                "coordinates": {
                  "latitude": 40.73061,
                  "longitude": -73.935242
                },
                "arrivalTimes": [
                  "08:15",
                  "08:45",
                  "09:15"
                ]
              },
              {
                "stopId": "3",
                "stopName": "Terminal Norte",
                "coordinates": {
                  "latitude": 40.748817,
                  "longitude": -73.985428
                },
                "arrivalTimes": [
                  "08:30",
                  "09:00",
                  "09:30"
                ]
              }
            ],
            "schedule": {
              "weekdays": {
                "startTime": "06:00",
                "endTime": "22:00",
                "frequencyMinutes": 15
              },
              "weekends": {
                "startTime": "07:00",
                "endTime": "20:00",
                "frequencyMinutes": 20
              }
            }
          }
        """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(routeJson)
                .addHeader("Content-Type", "application/json"));

        Mono<Route> routeMono = routeService.getRouteById("1");
        Route route = routeMono.block();

        assertNotNull(route);
        assertEquals("Ruta Centro-Norte", route.getRouteName());
    }

    @Test
    @DisplayName("When fetching a non-existent route by ID, then an empty result is returned")
    void testGetRouteByIdNotFound() {
        // Simula un error 404
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
                .addHeader("Content-Type", "application/json"));

        Mono<Route> routeMono = routeService.getRouteById("999");
        Route route = routeMono.block();

        assertNull(route);
    }

    @Test
    @DisplayName("When fetching a route by ID and a server error occurs, then an empty result is returned")
    void testGetRouteByIdServerError() {
        // Simula un error 500
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .addHeader("Content-Type", "application/json"));

        Mono<Route> routeMono = routeService.getRouteById("1");
        Route route = routeMono.block();

        assertNull(route);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }
}
