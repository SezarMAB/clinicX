package sy.sezar.clinicx.patient.model.enums;

/**
 * Represents the lifecycle status of a dental procedure.
 * Follows a state machine pattern with valid transitions.
 */
public enum ProcedureStatus {
    /**
     * Procedure is planned but not yet started
     */
    PLANNED("Planned", true, false),
    
    /**
     * Procedure is currently in progress
     */
    IN_PROGRESS("In Progress", true, false),
    
    /**
     * Procedure sent to external lab (for crowns, bridges, etc.)
     */
    SENT_TO_LAB("Sent to Lab", true, false),
    
    /**
     * Lab work received back from external lab
     */
    RECEIVED_FROM_LAB("Received from Lab", true, false),
    
    /**
     * Procedure successfully completed
     */
    COMPLETED("Completed", false, true),
    
    /**
     * Procedure was cancelled
     */
    CANCELLED("Cancelled", false, true);

    private final String displayName;
    private final boolean canModify;
    private final boolean isFinal;

    ProcedureStatus(String displayName, boolean canModify, boolean isFinal) {
        this.displayName = displayName;
        this.canModify = canModify;
        this.isFinal = isFinal;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canModify() {
        return canModify;
    }

    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Validates if transition to target status is allowed
     */
    public boolean canTransitionTo(ProcedureStatus target) {
        if (this == target || this.isFinal) {
            return false;
        }

        return switch (this) {
            case PLANNED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == SENT_TO_LAB || target == COMPLETED || target == CANCELLED;
            case SENT_TO_LAB -> target == RECEIVED_FROM_LAB || target == CANCELLED;
            case RECEIVED_FROM_LAB -> target == COMPLETED || target == CANCELLED;
            default -> false;
        };
    }

    /**
     * Check if this status indicates lab work is involved
     */
    public boolean isLabRelated() {
        return this == SENT_TO_LAB || this == RECEIVED_FROM_LAB;
    }
}