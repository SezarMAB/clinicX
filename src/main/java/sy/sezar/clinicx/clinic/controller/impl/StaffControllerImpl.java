package sy.sezar.clinicx.clinic.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sy.sezar.clinicx.clinic.controller.api.StaffControllerApi;
import sy.sezar.clinicx.clinic.dto.StaffCreateRequest;
import sy.sezar.clinicx.clinic.dto.StaffDto;
import sy.sezar.clinicx.clinic.dto.StaffSearchCriteria;
import sy.sezar.clinicx.clinic.dto.StaffUpdateRequest;
import sy.sezar.clinicx.clinic.model.enums.StaffRole;
import sy.sezar.clinicx.clinic.service.StaffService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StaffControllerImpl implements StaffControllerApi {

    private final StaffService staffService;

    @Override
    public ResponseEntity<StaffDto> getStaffById(UUID id) {
        log.info("Retrieving staff member with ID: {}", id);

        try {
            StaffDto staff = staffService.findStaffById(id);
            log.info("Successfully retrieved staff member with ID: {} - Status: 200 OK", id);
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            log.error("Failed to retrieve staff member with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<StaffDto>> getAllStaff(Pageable pageable) {
        log.info("Retrieving all staff members with pagination: {}", pageable);

        try {
            Page<StaffDto> staff = staffService.findAllStaff(pageable);
            log.info("Successfully retrieved {} staff members - Status: 200 OK", staff.getNumberOfElements());
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            log.error("Failed to retrieve staff members - Error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<StaffDto>> getActiveStaff(Pageable pageable) {
        log.info("Retrieving active staff members with pagination: {}", pageable);

        try {
            Page<StaffDto> staff = staffService.findAllActiveStaff(pageable);
            log.info("Successfully retrieved {} active staff members - Status: 200 OK", staff.getNumberOfElements());
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            log.error("Failed to retrieve active staff members - Error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<StaffDto>> getStaffByRole(StaffRole role, Pageable pageable) {
        log.info("Retrieving staff members by role: {} with pagination: {}", role, pageable);

        try {
            Page<StaffDto> staff = staffService.findStaffByRole(role, pageable);
            log.info("Successfully retrieved {} staff members with role {} - Status: 200 OK",
                    staff.getNumberOfElements(), role);
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            log.error("Failed to retrieve staff members by role: {} - Error: {}", role, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<StaffDto>> searchStaff(String searchTerm, Pageable pageable) {
        log.info("Searching staff members with term: '{}' and pagination: {}", searchTerm, pageable);

        try {
            Page<StaffDto> staff = staffService.searchStaff(searchTerm, pageable);
            log.info("Successfully found {} staff members for search term '{}' - Status: 200 OK",
                    staff.getNumberOfElements(), searchTerm);
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            log.error("Failed to search staff members with term: '{}' - Error: {}", searchTerm, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Page<StaffDto>> advancedSearchStaff(StaffSearchCriteria criteria, Pageable pageable) {
        log.info("Advanced search for staff members with criteria: {}", criteria);

        try {
            Page<StaffDto> staff = staffService.searchStaff(criteria, pageable);
            log.info("Advanced search found {} staff members - Status: 200 OK", staff.getNumberOfElements());
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            log.error("Failed to perform advanced search for staff members - Error: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<StaffDto> createStaff(StaffCreateRequest request) {
        log.info("Creating new staff member with name: {} and role: {}", request.getFullName(), request.getRole());
        log.debug("Staff creation request: {}", request);

        try {
            StaffDto staff = staffService.createStaff(request);
            log.info("Successfully created staff member: {} - Status: 201 CREATED", staff.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(staff);
        } catch (Exception e) {
            log.error("Failed to create staff member with name: {} - Error: {}",
                    request.getFullName(), e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<StaffDto> updateStaff(UUID id, StaffUpdateRequest request) {
        log.info("Updating staff member with ID: {}", id);
        log.debug("Staff update request: {}", request);

        try {
            StaffDto staff = staffService.updateStaff(id, request);
            log.info("Successfully updated staff member with ID: {} - Status: 200 OK", id);
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            log.error("Failed to update staff member with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public ResponseEntity<Void> deleteStaff(UUID id) {
        log.info("Deactivating staff member with ID: {}", id);

        try {
            staffService.deactivateStaff(id);
            log.info("Successfully deactivated staff member with ID: {} - Status: 204 NO CONTENT", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to deactivate staff member with ID: {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }
}
