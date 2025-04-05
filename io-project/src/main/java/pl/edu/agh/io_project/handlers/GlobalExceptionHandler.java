package pl.edu.agh.io_project.handlers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import pl.edu.agh.io_project.exceptions.AIFailureException;
import pl.edu.agh.io_project.exceptions.ValidationException;
import pl.edu.agh.io_project.responses.ErrorResponse;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException exp) {
        Map<String, String> error = Map.of("error", exp.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(new ErrorResponse(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        var errors = new HashMap<String, String>();
        exp.getBindingResult().getAllErrors().forEach(error -> {
            var fieldName = ((FieldError) error).getField();
            var errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(errors));
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationFailedException(ValidationException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                Map.of(
                        "message", exception.getMessage(),
                        "errors", exception.getErrors() != null
                                ? String.join("\n ", exception.getErrors())
                                : "no errors"
                )
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }


    @ExceptionHandler(AIFailureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleAIFailureException(AIFailureException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                Map.of(
                        "message", exception.getMessage(),
                        "errors", exception.getErrors() != null
                                ? String.join("\n ", exception.getErrors())
                                : "no errors"
                )
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exp) {
        Map<String, String> errors = Map.of("error", "Access denied: " + exp.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(new ErrorResponse(errors));
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleAsyncTimeout(AsyncRequestTimeoutException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                Map.of(
                        "message", "Asynchronous request timed out. Please try again later."
                )
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenericException(Exception exp) {
//        Map<String, String> errors = Map.of("error", "Internal Server Error: " + exp.getMessage());
//        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorResponse(errors));
//    }
}