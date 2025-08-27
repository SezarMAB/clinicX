package sy.sezar.clinicx.patient.model.enums;

/**
 * Enumeration for payment plan status.
 */
public enum PaymentPlanStatus {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    DEFAULTED("Defaulted"),
    SUSPENDED("Suspended");

    private final String displayName;

    PaymentPlanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
