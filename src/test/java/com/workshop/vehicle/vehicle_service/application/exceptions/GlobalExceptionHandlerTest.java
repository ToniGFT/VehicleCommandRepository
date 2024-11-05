package com.workshop.vehicle.vehicle_service.application.exceptions;

import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    @Test
    public void testHandleHttpMessageConversionException() {
        HttpMessageConversionException exception = new HttpMessageConversionException("Invalid JSON");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleHttpMessageConversionException(exception, webRequest);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertEquals("Invalid JSON format: Invalid JSON", entity.getBody().getMessage());
    }

    @Test
    public void testHandleNumberFormatException() {
        NumberFormatException exception = new NumberFormatException("Invalid number");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleNumberFormatException(exception, webRequest);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertEquals("Invalid input", entity.getBody().getMessage());
    }

    @Test
    public void testHandleMethodArgumentNotValid() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("vehicle", "licensePlate", "License plate cannot be empty"));
        fieldErrors.add(new FieldError("vehicle", "capacity", "Capacity is required"));

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleMethodArgumentNotValid(exception);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
        assertEquals("Validation failed: License plate cannot be empty; Capacity is required; ", entity.getBody().getMessage());
    }

    @Test
    public void testHandleNoSuchElementException() {
        NoSuchElementException exception = new NoSuchElementException("Element not found");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleNoSuchElementException(exception, webRequest);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
        assertEquals("Element not found", entity.getBody().getMessage());
    }

    @Test
    public void testHandleInternalException() {
        ExecutionControl.InternalException exception = new ExecutionControl.InternalException("Internal error");

        Mono<ResponseEntity<ErrorResponse>> response = globalExceptionHandler.handleInternalException(exception, webRequest);

        ResponseEntity<ErrorResponse> entity = response.block();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
        assertEquals("Internal error", entity.getBody().getMessage());
    }
}