package com.evaap.dto.response;

import lombok.Getter;

import java.time.Instant;

/**
 * Generic envelope so every endpoint returns a consistent shape:
 * { success, message, data, timestamp }
 * Makes frontend error handling and QA's Postman assertions predictable
 * across every endpoint instead of each one inventing its own shape.
 */
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final Instant timestamp = Instant.now();

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
