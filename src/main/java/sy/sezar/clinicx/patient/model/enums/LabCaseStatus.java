package sy.sezar.clinicx.patient.model.enums;

/**
 * Represents the status of a lab case for external dental work.
 * Tracks the lifecycle of items sent to dental laboratories.
 */
public enum LabCaseStatus {
    /**
     * Case has been sent to the lab
     */
    SENT("Sent to Lab"),
    
    /**
     * Lab is processing the case
     */
    IN_PROGRESS("In Progress at Lab"),
    
    /**
     * Case received back from lab
     */
    RECEIVED("Received from Lab"),
    
    /**
     * Case delivered to patient
     */
    DELIVERED("Delivered to Patient"),
    
    /**
     * Case was cancelled
     */
    CANCELLED("Cancelled"),
    
    /**
     * Case rejected due to quality issues
     */
    REJECTED("Rejected - Quality Issue"),
    
    /**
     * Case needs to be remade
     */
    REMAKE("Remake Required");

    private final String displayName;

    LabCaseStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if the lab case is in a terminal state
     */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }

    /**
     * Check if the lab case is complete and ready for patient
     */
    public boolean isComplete() {
        return this == RECEIVED || this == DELIVERED;
    }

    /**
     * Validates if transition to target status is allowed
     */
    public boolean canTransitionTo(LabCaseStatus target) {
        if (this == target || this.isTerminal()) {
            return false;
        }

        return switch (this) {
            case SENT -> target == IN_PROGRESS || target == RECEIVED || target == CANCELLED;
            case IN_PROGRESS -> target == RECEIVED || target == CANCELLED;
            case RECEIVED -> target == DELIVERED || target == REJECTED;
            case REJECTED -> target == REMAKE || target == CANCELLED;
            case REMAKE -> target == SENT;
            default -> false;
        };
    }
}