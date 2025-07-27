package sy.sezar.clinicx.core.exception;

/**
 * Exception thrown when a value doesn't meet validation requirements.
 * Provides detailed information about what value is invalid and the expected format.
 */
public class NotValidValueException extends BusinessRuleException {
    
    private final String fieldName;
    private final Object invalidValue;
    private final String expectedFormat;
    
    public NotValidValueException(String fieldName, Object invalidValue, String expectedFormat) {
        super(String.format("Invalid value for field '%s': '%s'. Expected format: %s", 
            fieldName, invalidValue, expectedFormat));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.expectedFormat = expectedFormat;
    }
    
    public NotValidValueException(String fieldName, Object invalidValue, String expectedFormat, String message) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.expectedFormat = expectedFormat;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Object getInvalidValue() {
        return invalidValue;
    }
    
    public String getExpectedFormat() {
        return expectedFormat;
    }
}