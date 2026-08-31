package com.example.textsocial.search.api;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.textsocial.search.service.InvalidCursorException;

@RestControllerAdvice
class RestExceptionHandler {
    record ApiError(String code, String message, Map<String, String> fieldErrors, Instant timestamp) {}
    @ExceptionHandler(InvalidCursorException.class)
    ResponseEntity<ApiError> cursor(InvalidCursorException ex) { return ResponseEntity.badRequest().body(new ApiError("INVALID_CURSOR", ex.getMessage(), Map.of(), Instant.now())); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiError("VALIDATION_FAILED", "Request validation failed", fields, Instant.now()));
    }
}
