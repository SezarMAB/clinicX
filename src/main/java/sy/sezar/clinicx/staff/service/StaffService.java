package sy.sezar.clinicx.staff.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sy.sezar.clinicx.staff.dto.StaffCreateRequest;
import sy.sezar.clinicx.staff.dto.StaffDto;
import sy.sezar.clinicx.staff.dto.StaffSearchCriteria;
import sy.sezar.clinicx.staff.dto.StaffUpdateRequest;
import sy.sezar.clinicx.staff.model.enums.StaffRole;

import java.util.UUID;

/**
 * Service interface for managing staff members.
 */
public interface StaffService {
    
    /**
     * Creates a new staff member.
     * @param request Create request containing staff information
     * @return Created StaffDto
     */
    StaffDto createStaff(StaffCreateRequest request);
    
    /**
     * Updates an existing staff member.
     * @param id Staff ID
     * @param request Update request containing new staff information
     * @return Updated StaffDto
     */
    StaffDto updateStaff(UUID id, StaffUpdateRequest request);
    
    /**
     * Finds a staff member by ID.
     * @param id Staff ID
     * @return StaffDto
     */
    StaffDto findStaffById(UUID id);
    
    /**
     * Finds all staff members with pagination.
     * @param pageable Pagination information
     * @return Page of StaffDto
     */
    Page<StaffDto> findAllStaff(Pageable pageable);
    
    /**
     * Finds all active staff members with pagination.
     * @param pageable Pagination information
     * @return Page of StaffDto
     */
    Page<StaffDto> findAllActiveStaff(Pageable pageable);
    
    /**
     * Finds staff members by role.
     * @param role Staff role
     * @param pageable Pagination information
     * @return Page of StaffDto
     */
    Page<StaffDto> findStaffByRole(StaffRole role, Pageable pageable);
    
    /**
     * Searches staff members based on criteria.
     * @param criteria Search criteria
     * @param pageable Pagination information
     * @return Page of StaffDto
     */
    Page<StaffDto> searchStaff(StaffSearchCriteria criteria, Pageable pageable);
    
    /**
     * Simple search for staff members.
     * @param searchTerm Search term
     * @param pageable Pagination information
     * @return Page of StaffDto
     */
    Page<StaffDto> searchStaff(String searchTerm, Pageable pageable);
    
    /**
     * Deactivates a staff member (soft delete).
     * @param id Staff ID
     */
    void deactivateStaff(UUID id);
}
