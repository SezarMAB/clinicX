package sy.sezar.clinicx.patient.model.enums;

/**
 * Enumeration for installment status.
 */
public enum InstallmentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    PARTIALLY_PAID("Partially Paid"),
    OVERDUE("Overdue"),
    DEFAULTED("Defaulted"),
    CANCELLED("Cancelled");

    private final String displayName;

    InstallmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
