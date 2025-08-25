package sy.sezar.clinicx.tenant.exception;

import sy.sezar.clinicx.clinic.model.enums.StaffRole;

/**
 * Exception thrown when role assignment operations fail.
 */
public class RoleAssignmentException extends RuntimeException {
    
    public RoleAssignmentException(String message) {
        super(message);
    }
    
    public RoleAssignmentException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static RoleAssignmentException insufficientPrivileges(StaffRole userRole, StaffRole targetRole) {
        return new RoleAssignmentException(
            String.format("User with role %s cannot assign role %s", userRole.name(), targetRole.name())
        );
    }
    
    public static RoleAssignmentException invalidRole(String roleName) {
        return new RoleAssignmentException(
            String.format("Invalid role: %s", roleName)
        );
    }
    
    public static RoleAssignmentException noRolesToAssign() {
        return new RoleAssignmentException("No valid roles provided for assignment");
    }
}