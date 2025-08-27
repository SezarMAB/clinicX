package sy.sezar.clinicx.patient.model.enums;

/**
 * Enumeration for payment methods supported by the system.
 */
public enum PaymentMethod {
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    BANK_TRANSFER("Bank Transfer"),
    CHECK("Check"),
    INSURANCE("Insurance"),
    PAYMENT_PLAN("Payment Plan"),
    ONLINE_PAYMENT("Online Payment"),
    MOBILE_PAYMENT("Mobile Payment"),
    OTHER("Other");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
