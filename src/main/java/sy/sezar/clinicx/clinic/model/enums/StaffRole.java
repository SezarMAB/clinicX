package sy.sezar.clinicx.clinic.model.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;

/**
 * Enumeration representing different staff roles within the clinic system.
 * This enum provides a hierarchical role system for access control and user management.
 *
 * <p>Role Hierarchy (from highest to lowest authority):</p>
 * <ul>
 *   <li>{@link #SUPER_ADMIN} - System-wide administrative privileges</li>
 *   <li>{@link #ADMIN} - Clinic administrative privileges</li>
 *   <li>{@link #DOCTOR} - Medical practitioner with patient care responsibilities</li>
 *   <li>{@link #NURSE} - Nursing staff with patient care support</li>
 *   <li>{@link #ASSISTANT} - Medical assistant supporting clinical operations</li>
 *   <li>{@link #RECEPTIONIST} - Front desk and appointment management</li>
 *   <li>{@link #ACCOUNTANT} - Financial and billing management</li>
 *   <li>{@link #EXTERNAL} - External consultants or temporary staff</li>
 *   <li>{@link #INTERNAL} - Internal staff without specific role assignment</li>
 * </ul>
 *
 * @author ClinicX Development Team
 * @since 1.0
 */
@Getter
public enum StaffRole {

    /** System administrator with full system privileges */
    SUPER_ADMIN("SUPER_ADMIN", "Super Administrator", 100),

    /** Clinic administrator with clinic-wide management privileges */
    ADMIN("ADMIN", "Administrator", 90),

    /** Medical doctor with patient care and treatment responsibilities */
    DOCTOR("DOCTOR", "Doctor", 80),

    /** Nursing staff providing patient care and medical support */
    NURSE("NURSE", "Nurse", 70),

    /** Medical assistant supporting clinical operations */
    ASSISTANT("ASSISTANT", "Medical Assistant", 60),

    /** Reception staff managing appointments and front desk operations */
    RECEPTIONIST("RECEPTIONIST", "Receptionist", 50),

    /** Accounting staff managing financial operations and billing */
    ACCOUNTANT("ACCOUNTANT", "Accountant", 40),

    /** External consultants or temporary staff members */
    EXTERNAL("EXTERNAL", "External Staff", 20),

    /** Internal staff without specific role classification */
    INTERNAL("INTERNAL", "Internal Staff", 10);

  /**
   * -- GETTER --
   *  Returns the role identifier.
   *
   * @return the role identifier string
   */
  private final String role;
  /**
   * -- GETTER --
   *  Returns the human-readable display name for this role.
   *
   * @return the display name
   */
  private final String displayName;
  /**
   * -- GETTER --
   *  Returns the hierarchy level of this role.
   *  Higher numbers indicate higher authority levels.
   *
   * @return the hierarchy level as an integer
   */
  private final int hierarchyLevel;

    /**
     * Constructs a StaffRole with the specified role identifier, display name, and hierarchy level.
     *
     * @param role the unique role identifier
     * @param displayName the human-readable display name for the role
     * @param hierarchyLevel the numerical hierarchy level (higher numbers indicate higher authority)
     */
    StaffRole(String role, String displayName, int hierarchyLevel) {
        this.role = role;
        this.displayName = displayName;
        this.hierarchyLevel = hierarchyLevel;
    }

  /**
     * Checks if this role has higher or equal authority compared to the specified role.
     *
     * @param other the role to compare against
     * @return true if this role has higher or equal authority, false otherwise
     * @throws IllegalArgumentException if the other role is null
     */
    public boolean hasAuthorityOver(StaffRole other) {
        if (other == null) {
            throw new IllegalArgumentException("Role to compare cannot be null");
        }
        return this.hierarchyLevel >= other.hierarchyLevel;
    }

    /**
     * Checks if this role is an administrative role (ADMIN or SUPER_ADMIN).
     *
     * @return true if this role is administrative, false otherwise
     */
    public boolean isAdministrative() {
        return this == ADMIN || this == SUPER_ADMIN;
    }

    /**
     * Checks if this role is a clinical role (DOCTOR, NURSE, or ASSISTANT).
     *
     * @return true if this role is clinical, false otherwise
     */
    public boolean isClinical() {
        return this == DOCTOR || this == NURSE || this == ASSISTANT;
    }

    /**
     * Converts a string representation to a StaffRole enum value.
     * This method performs case-insensitive matching against both role identifiers and display names.
     *
     * @param roleString the string representation of the role
     * @return the corresponding StaffRole enum value
     * @throws IllegalArgumentException if no matching role is found or if the input is null/empty
     */
    public static StaffRole fromString(String roleString) {
        if (roleString == null || roleString.trim().isEmpty()) {
            throw new IllegalArgumentException("Role string cannot be null or empty");
        }

        String trimmedRole = roleString.trim();

        return Arrays.stream(StaffRole.values())
                .filter(staffRole ->
                    staffRole.role.equalsIgnoreCase(trimmedRole) ||
                    staffRole.displayName.equalsIgnoreCase(trimmedRole))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    String.format("No enum constant found for role: '%s'. Valid roles are: %s",
                        trimmedRole, Arrays.toString(StaffRole.values()))));
    }

    /**
     * Attempts to convert a string representation to a StaffRole enum value.
     * Returns an Optional that contains the StaffRole if found, or empty if not found.
     *
     * @param roleString the string representation of the role
     * @return an Optional containing the StaffRole if found, empty otherwise
     */
    public static Optional<StaffRole> fromStringOptional(String roleString) {
        if (roleString == null || roleString.trim().isEmpty()) {
            return Optional.empty();
        }

        String trimmedRole = roleString.trim();

        return Arrays.stream(StaffRole.values())
                .filter(staffRole ->
                    staffRole.role.equalsIgnoreCase(trimmedRole) ||
                    staffRole.displayName.equalsIgnoreCase(trimmedRole))
                .findFirst();
    }

    /**
     * Returns the string representation of this enum value.
     * This returns the role identifier, not the display name.
     *
     * @return the role identifier string
     */
    @Override
    public String toString() {
        return role;
    }
}
