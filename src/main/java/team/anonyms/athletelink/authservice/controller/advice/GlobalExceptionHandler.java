package team.anonyms.athletelink.authservice.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.anonyms.athletelink.authservice.exception.EmailAlreadyExistsException;
import team.anonyms.athletelink.authservice.exception.VerificationCodeAlreadyExistsException;
import team.anonyms.athletelink.authservice.exception.IncorrectPasswordException;
import team.anonyms.athletelink.authservice.exception.InsecureDirectObjectReferenceException;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code GlobalExceptionHandler} is dedicated to centrally handle all uncaught runtime exceptions in the application.
 */
@RestControllerAdvice
@Slf4j
/* TODO: fix return value in exceptions. Add necessary return content according to https://datatracker.ietf.org/doc/html/rfc91107
    Also make the error message explicit and understandable for a user.
 */
@SuppressWarnings(value = {"unused"})
public class GlobalExceptionHandler {
    @ExceptionHandler(exception = IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error(e.getMessage());
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

        log.error("Validation errors: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(exception = IncorrectPasswordException.class)
    public ResponseEntity<Void> handleIncorrectPasswordException(IncorrectPasswordException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(exception = EmailAlreadyExistsException.class)
    public ResponseEntity<Void> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(exception = VerificationCodeAlreadyExistsException.class)
    public ResponseEntity<Void> handleCodeAlreadyExistsException(VerificationCodeAlreadyExistsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(exception = InsecureDirectObjectReferenceException.class)
    public ResponseEntity<Void> handleInsecureDirectObjectReferenceException(InsecureDirectObjectReferenceException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
