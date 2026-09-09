package com.enviro.assessment.junior.fezile.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * NOTE: this is deliberately minimal — it only maps
 * {@link ResourceNotFoundException} to a 404 with a useful body, to satisfy
 * the brief's "proper error handling and user feedback required" business
 * rule. It is NOT the full "Global exception handling" advanced requirement
 * (which would also cover validation errors, generic 500s, etc.) — that
 * wasn't one of the three advanced items chosen for this submission
 * (Unit tests / Input validation / UI validation were chosen instead).
 * Validation errors are handled separately where @Valid is used.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
