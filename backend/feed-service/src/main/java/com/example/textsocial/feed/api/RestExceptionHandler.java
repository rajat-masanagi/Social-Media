package com.example.textsocial.feed.api;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class RestExceptionHandler {
    record ApiError(String code, String message, Map<String, String> fieldErrors, Instant timestamp) {}
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> invalid(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ApiError("INVALID_REQUEST", ex.getMessage(), Map.of(), Instant.now()));
    }
}
