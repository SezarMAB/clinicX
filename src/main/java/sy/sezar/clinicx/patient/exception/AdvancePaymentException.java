package sy.sezar.clinicx.patient.exception;

import sy.sezar.clinicx.core.exception.BusinessRuleException;

/**
 * Exception thrown when advance payment operations fail.
 */
public class AdvancePaymentException extends BusinessRuleException {

    public AdvancePaymentException(String message) {
        super(message);
    }

    public AdvancePaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}