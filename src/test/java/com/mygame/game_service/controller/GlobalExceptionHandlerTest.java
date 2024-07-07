package com.mygame.game_service.controller;

import com.mygame.game_service.dto.ErrorResponseDto;
import com.mygame.game_service.exception.GenericException;
import com.mygame.game_service.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleValidationException() {
        ValidationException ex = new ValidationException("Validation error");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getErrorCode());
    }

    @Test
    public void testHandleGenericException() {
        GenericException ex = new GenericException("Generic error");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Generic error", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getErrorCode());
    }

    @Test
    public void testHandleException() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unexpected error", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getErrorCode());
    }
}