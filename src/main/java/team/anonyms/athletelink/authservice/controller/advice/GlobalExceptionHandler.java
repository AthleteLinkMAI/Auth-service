package team.anonyms.athletelink.authservice.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.anonyms.athletelink.authservice.exception.CodeAlreadyExistsException;
import team.anonyms.athletelink.authservice.exception.IncorrectPasswordException;
import team.anonyms.athletelink.authservice.exception.InsecureDirectObjectReferenceException;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code GlobalExceptionHandler} is dedicated to centrally handle all uncaught runtime exceptions in the application.
 */
@RestControllerAdvice
@SuppressWarnings(value = {"unused"})
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(exception = IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error(e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        logger.error(e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(exception = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.error("Validation errors: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(exception = IncorrectPasswordException.class)
    public ResponseEntity<Void> handleIncorrectPasswordException(IncorrectPasswordException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(exception = CodeAlreadyExistsException.class)
    public ResponseEntity<Void> handleCodeAlreadyExistsException(CodeAlreadyExistsException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(exception = InsecureDirectObjectReferenceException.class)
    public ResponseEntity<Void> handleInsecureDirectObjectReferenceException(InsecureDirectObjectReferenceException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
