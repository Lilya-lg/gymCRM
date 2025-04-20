package uz.gym.training.util;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uz.gym.training.util.exceptions.InsufficientDurationException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleEntityNotFoundException(
      EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("status", "error", "message", ex.getMessage()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("status", "error", "message", "Internal server error: " + ex.getMessage()));
  }
  @ExceptionHandler(InsufficientDurationException.class)
  public ResponseEntity<Map<String, String>> handleInsufficientDuration(
          InsufficientDurationException ex) {
    return ResponseEntity.badRequest()
            .body(Map.of("status", "error", "message", ex.getMessage()));
  }
}
