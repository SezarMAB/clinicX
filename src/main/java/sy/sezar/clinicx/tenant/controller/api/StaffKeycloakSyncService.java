package sy.sezar.clinicx.tenant.controller.api;

import sy.sezar.clinicx.clinic.dto.StaffCreateRequest;
import sy.sezar.clinicx.clinic.dto.StaffDto;
import sy.sezar.clinicx.clinic.model.Staff;

/**
 * Service to handle synchronization between Staff records and Keycloak users.
 */
public interface StaffKeycloakSyncService {

    /**
     * Create a Staff record with an associated Keycloak user.
     *
     * @param request the staff creation request
     * @param password the initial password for the Keycloak user
     * @param createKeycloakUser whether to create a Keycloak user
     * @return the created staff DTO
     */
    StaffDto createStaffWithKeycloakUser(StaffCreateRequest request, String password, boolean createKeycloakUser);

    /**
     * Sync an existing Staff record with Keycloak.
     * Creates a Keycloak user if one doesn't exist.
     *
     * @param staff the staff record
     * @param password the password for the new Keycloak user
     * @return the Keycloak user ID
     */
    String syncStaffToKeycloak(Staff staff, String password);

    /**
     * Update Staff record when Keycloak user is updated.
     *
     * @param userId the Keycloak user ID
     * @param tenantId the tenant ID
     */
    void updateStaffFromKeycloak(String userId, String tenantId);

    /**
     * Check if a Staff record has an associated Keycloak user.
     *
     * @param staffId the staff ID
     * @return true if Keycloak user exists
     */
    boolean hasKeycloakUser(String staffId);
}
