package com.example.textsocial.feed.api;

import com.example.textsocial.feed.service.FeedService.LabNotImplementedException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class RestExceptionHandler {
    record ApiError(String code, String message, Map<String, String> fieldErrors, Instant timestamp) {}
    @ExceptionHandler(LabNotImplementedException.class)
    ResponseEntity<ApiError> unfinished(LabNotImplementedException ex) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new ApiError("LAB_NOT_IMPLEMENTED", ex.getMessage(), Map.of(), Instant.now()));
    }
}

