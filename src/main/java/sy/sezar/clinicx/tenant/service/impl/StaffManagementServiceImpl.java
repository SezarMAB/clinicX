package sy.sezar.clinicx.tenant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sy.sezar.clinicx.clinic.model.Staff;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.clinic.repository.StaffRepository;
import sy.sezar.clinicx.core.exception.BusinessRuleException;
import sy.sezar.clinicx.core.exception.ResourceNotFoundException;
import sy.sezar.clinicx.tenant.constants.TenantConstants;
import sy.sezar.clinicx.tenant.service.StaffManagementService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of StaffManagementService.
 * Handles all Staff entity operations with proper transaction management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StaffManagementServiceImpl implements StaffManagementService {
    
    private final StaffRepository staffRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<Staff> findByTenantId(String tenantId) {
        return staffRepository.findByTenantId(tenantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> findByKeycloakUserIdAndTenantId(String keycloakUserId, String tenantId) {
        return staffRepository.findByKeycloakUserIdAndTenantId(keycloakUserId, tenantId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Staff> findByKeycloakUserId(String keycloakUserId) {
        return staffRepository.findByKeycloakUserId(keycloakUserId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return staffRepository.existsByEmailIgnoreCase(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByKeycloakUserIdAndTenantId(String keycloakUserId, String tenantId) {
        return staffRepository.existsByKeycloakUserIdAndTenantId(keycloakUserId, tenantId);
    }
    
    @Override
    @Transactional
    public Staff createStaff(String tenantId, String keycloakUserId, String fullName, 
                           String email, String phoneNumber, Set<StaffRole> roles) {
        Staff staff = new Staff();
        staff.setTenantId(tenantId);
        staff.setKeycloakUserId(keycloakUserId);
        staff.setFullName(fullName);
        staff.setEmail(email);
        staff.setPhoneNumber(phoneNumber);
        staff.setRoles(roles != null ? new HashSet<>(roles) : new HashSet<>());
        staff.setActive(true);
        
        Staff savedStaff = staffRepository.save(staff);
        log.info(TenantConstants.LOG_CREATED_STAFF, fullName, savedStaff.getId());
        return savedStaff;
    }
    
    @Override
    @Transactional
    public Staff updateStaff(Staff staff) {
        return staffRepository.save(staff);
    }
    
    @Override
    @Transactional
    public int updatePhoneNumber(String keycloakUserId, String tenantId, String phoneNumber) {
        return staffRepository.updatePhoneNumberByKeycloakUserIdAndTenantId(
            keycloakUserId, tenantId, phoneNumber);
    }
    
    @Override
    @Transactional
    public Staff updateStaffRoles(String keycloakUserId, String tenantId, Set<StaffRole> roles) {
        Staff staff = findByKeycloakUserIdAndTenantId(keycloakUserId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(TenantConstants.ERROR_USER_NOT_IN_TENANT)));
        
        staff.setRoles(roles != null ? new HashSet<>(roles) : new HashSet<>());
        Staff updatedStaff = staffRepository.save(staff);
        log.info(TenantConstants.LOG_UPDATED_STAFF_ROLE, keycloakUserId);
        return updatedStaff;
    }
    
    @Override
    @Transactional
    public Staff deactivateStaff(String keycloakUserId, String tenantId) {
        Staff staff = findByKeycloakUserIdAndTenantId(keycloakUserId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(TenantConstants.ERROR_USER_NOT_IN_TENANT)));
        
        validateDeactivation(staff);
        
        staff.setActive(false);
        Staff deactivatedStaff = staffRepository.save(staff);
        log.info(TenantConstants.LOG_DEACTIVATED_STAFF, keycloakUserId);
        return deactivatedStaff;
    }
    
    @Override
    @Transactional
    public Staff activateStaff(String keycloakUserId, String tenantId) {
        Staff staff = findByKeycloakUserIdAndTenantId(keycloakUserId, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(TenantConstants.ERROR_USER_NOT_IN_TENANT)));
        
        staff.setActive(true);
        Staff activatedStaff = staffRepository.save(staff);
        log.info(TenantConstants.LOG_ACTIVATED_STAFF, keycloakUserId);
        return activatedStaff;
    }
    
    @Override
    @Transactional
    public void deactivateAllStaffForUser(String keycloakUserId) {
        List<Staff> staffRecords = findByKeycloakUserId(keycloakUserId);
        for (Staff staff : staffRecords) {
            staff.setActive(false);
        }
        staffRepository.saveAll(staffRecords);
        log.info("Deactivated {} Staff records for user {}", staffRecords.size(), keycloakUserId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getTenantIdsForUser(String keycloakUserId) {
        return staffRepository.findTenantIdsByKeycloakUserId(keycloakUserId);
    }
    
    @Override
    public void validateDeactivation(Staff staff) {
        if (staff.getRoles() != null && staff.getRoles().contains(StaffRole.ADMIN)) {
            throw new BusinessRuleException(TenantConstants.ERROR_CANNOT_DEACTIVATE_ADMIN);
        }
    }
    
    @Override
    @Transactional
    public Staff createOrReactivateExternalStaff(String tenantId, String keycloakUserId, 
                                                String fullName, String email, 
                                                String phoneNumber, Set<StaffRole> roles) {
        Optional<Staff> existingStaff = findByKeycloakUserIdAndTenantId(keycloakUserId, tenantId);
        
        if (existingStaff.isPresent()) {
            Staff staff = existingStaff.get();
            if (!staff.isActive()) {
                staff.setActive(true);
                staff.setRoles(roles != null ? new HashSet<>(roles) : new HashSet<>());
                Staff reactivatedStaff = staffRepository.save(staff);
                log.info("Reactivated external Staff record for user {} in tenant {}", 
                        keycloakUserId, tenantId);
                return reactivatedStaff;
            }
            throw new BusinessRuleException(TenantConstants.ERROR_USER_ALREADY_HAS_ACCESS);
        }
        
        return createStaff(tenantId, keycloakUserId, fullName, email, phoneNumber, roles);
    }
}