package com.example.textsocial.social.api;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(UsernameTakenException.class)
    ResponseEntity<ApiError> usernameTaken(UsernameTakenException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError("USERNAME_TAKEN", ex.getMessage(), Map.of("username", ex.getMessage()), Instant.now()));
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiError> authentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("BAD_CREDENTIALS", ex.getMessage(), Map.of(), Instant.now()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("NOT_FOUND", ex.getMessage(), Map.of(), Instant.now()));
    }
    @ExceptionHandler(SelfFollowException.class)
    ResponseEntity<ApiError> selfFollow(SelfFollowException ex) { return ResponseEntity.badRequest().body(new ApiError("SELF_FOLLOW", ex.getMessage(), Map.of(), Instant.now())); }
    @ExceptionHandler(InvalidCursorException.class)
    ResponseEntity<ApiError> cursor(InvalidCursorException ex) { return ResponseEntity.badRequest().body(new ApiError("INVALID_CURSOR", ex.getMessage(), Map.of(), Instant.now())); }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ApiError("VALIDATION_FAILED", "Request validation failed", fields, Instant.now()));
    }
}
