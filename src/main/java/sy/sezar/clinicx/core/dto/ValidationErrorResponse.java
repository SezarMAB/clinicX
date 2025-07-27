package sy.sezar.clinicx.core.dto;

/**
 * Response DTO for validation errors sent to the frontend.
 * Contains field-specific validation error information.
 */
public class ValidationErrorResponse {
    
    private String fieldName;
    private Object invalidValue;
    private String expectedFormat;
    private String message;
    private String errorCode;
    
    public ValidationErrorResponse() {
    }
    
    public ValidationErrorResponse(String fieldName, Object invalidValue, String expectedFormat, String message) {
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.expectedFormat = expectedFormat;
        this.message = message;
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public Object getInvalidValue() {
        return invalidValue;
    }
    
    public void setInvalidValue(Object invalidValue) {
        this.invalidValue = invalidValue;
    }
    
    public String getExpectedFormat() {
        return expectedFormat;
    }
    
    public void setExpectedFormat(String expectedFormat) {
        this.expectedFormat = expectedFormat;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}