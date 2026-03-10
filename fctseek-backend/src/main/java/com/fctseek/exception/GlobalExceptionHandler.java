/*
 * Click nbfs:nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs:nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.fctseek.exception;

import com.fctseek.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones.
 * Captura todas las excepciones y devuelve respuestas JSON formateadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja BadRequestException (400)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequestException(
            BadRequestException ex, WebRequest request) {

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja UnauthorizedException (401)
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja errores de autenticación (401)
     */
    @ExceptionHandler({ AuthenticationException.class, BadCredentialsException.class })
    public ResponseEntity<ApiResponse> handleAuthenticationException(
            Exception ex, WebRequest request) {

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message("Credenciales inválidas")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja errores de acceso denegado (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message("No tienes permisos para realizar esta acción")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja errores de validación de campos (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Error de validación");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja cualquier otra excepción no controlada (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        // Log del error para debugging
        ex.printStackTrace();

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message("Error interno del servidor: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
