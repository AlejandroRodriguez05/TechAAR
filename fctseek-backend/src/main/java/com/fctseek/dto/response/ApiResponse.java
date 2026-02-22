package com.fctseek.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * DTO genérico para respuestas de la API.
 * Usado especialmente para mensajes de éxito/error.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private Boolean success;
    private String message;
    private Object data;
    private LocalDateTime timestamp;

    // Constructores
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Boolean success;
        private String message;
        private Object data;
        private LocalDateTime timestamp;

        public Builder success(Boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiResponse build() {
            ApiResponse response = new ApiResponse();
            response.success = this.success;
            response.message = this.message;
            response.data = this.data;
            response.timestamp = this.timestamp != null ? this.timestamp : LocalDateTime.now();
            return response;
        }
    }

    // Métodos estáticos de conveniencia
    public static ApiResponse ok(String message) {
        return new ApiResponse(true, message);
    }

    public static ApiResponse ok(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }

    // Getters y Setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
