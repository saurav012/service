package com.weather.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientException;

@ControllerAdvice
public class ClimateExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ClimateExceptionHandler.class);

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<String> errorResolve(WebClientException ex) {
        logger.error("WebClient response error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }
}

