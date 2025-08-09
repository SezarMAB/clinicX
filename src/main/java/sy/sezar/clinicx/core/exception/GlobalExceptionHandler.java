package sy.sezar.clinicx.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import sy.sezar.clinicx.core.dto.ValidationErrorResponse;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex) {
        log.error("Business rule violation: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Business Rule Violation",
            ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotValidValueException.class)
    public ResponseEntity<ValidationErrorResponse> handleNotValidValueException(NotValidValueException ex) {
        log.error("Validation error for field '{}': {}", ex.getFieldName(), ex.getMessage());
        ValidationErrorResponse response = new ValidationErrorResponse(
            ex.getFieldName(),
            ex.getInvalidValue(),
            ex.getExpectedFormat(),
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String expectedFormat = "";
        
        // Determine expected format based on the required type
        if (ex.getRequiredType() != null) {
            if (Instant.class.isAssignableFrom(ex.getRequiredType())) {
                expectedFormat = "ISO 8601 format (e.g., 2024-01-15T10:00:00Z)";
            } else if (java.time.LocalDate.class.isAssignableFrom(ex.getRequiredType())) {
                expectedFormat = "YYYY-MM-DD format (e.g., 2024-01-15)";
            } else {
                expectedFormat = ex.getRequiredType().getSimpleName();
            }
        }
        
        log.error("Type mismatch for parameter '{}': {}", paramName, ex.getMessage());
        ValidationErrorResponse response = new ValidationErrorResponse(
            paramName,
            invalidValue,
            expectedFormat,
            String.format("Invalid format for parameter '%s'. Expected %s", paramName, expectedFormat)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ValidationErrorResponse> handleDateTimeParse(DateTimeParseException ex) {
        log.error("Date/time parse error: {}", ex.getMessage());
        ValidationErrorResponse response = new ValidationErrorResponse(
            "dateTime",
            ex.getParsedString(),
            "ISO 8601 format (e.g., 2024-01-15T10:00:00Z)",
            "Invalid date/time format: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Data validation failed";
        
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("chk_email_format")) {
                message = "Invalid email format. Email must be in the format: user@domain.com";
            } else if (ex.getMessage().contains("uk_patients_public_facing_id")) {
                message = "A patient with this ID already exists";
            } else if (ex.getMessage().contains("patients_email_key")) {
                message = "A patient with this email already exists";
            } else if (ex.getMessage().contains("chk_phone_format")) {
                message = "Invalid phone number format. Phone number must start with + followed by digits";
            } else if (ex.getMessage().contains("foreign key")) {
                message = "Cannot perform this operation due to related records";
            } else if (ex.getMessage().contains("not-null")) {
                message = "Required field is missing";
            } else {
                log.error("Data integrity violation: {}", ex.getMessage());
                message = "Data validation failed. Please check your input and try again";
            }
        }
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            message,
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Invalid input data: " + errors,
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp
    ) {}
}