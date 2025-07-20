package sy.sezar.clinicx.staff.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.staff.dto.StaffCreateRequest;
import sy.sezar.clinicx.staff.dto.StaffDto;
import sy.sezar.clinicx.staff.dto.StaffSearchCriteria;
import sy.sezar.clinicx.staff.dto.StaffUpdateRequest;
import sy.sezar.clinicx.staff.model.enums.StaffRole;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff", description = "Operations related to staff management")
public interface StaffControllerApi {
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get staff member by ID",
        description = "Retrieves a staff member by their unique identifier."
    )
    @ApiResponse(responseCode = "200", description = "Staff member found",
                content = @Content(schema = @Schema(implementation = StaffDto.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    ResponseEntity<StaffDto> getStaffById(
            @Parameter(description = "Staff ID") @PathVariable UUID id);
    
    @GetMapping
    @Operation(
        summary = "Get all staff members",
        description = "Retrieves paginated list of all staff members.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: fullName", example = "fullName")
        }
    )
    @ApiResponse(responseCode = "200", description = "Staff members retrieved")
    ResponseEntity<Page<StaffDto>> getAllStaff(
            @Parameter(hidden = true) @PageableDefault(sort = "fullName") Pageable pageable);
    
    @GetMapping("/active")
    @Operation(
        summary = "Get active staff members",
        description = "Retrieves paginated list of active staff members.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: fullName", example = "fullName")
        }
    )
    @ApiResponse(responseCode = "200", description = "Active staff members retrieved")
    ResponseEntity<Page<StaffDto>> getActiveStaff(
            @Parameter(hidden = true) @PageableDefault(sort = "fullName") Pageable pageable);
    
    @GetMapping("/by-role/{role}")
    @Operation(
        summary = "Get staff members by role",
        description = "Retrieves paginated list of staff members with a specific role.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: fullName", example = "fullName")
        }
    )
    @ApiResponse(responseCode = "200", description = "Staff members retrieved")
    ResponseEntity<Page<StaffDto>> getStaffByRole(
            @Parameter(description = "Staff role") @PathVariable StaffRole role,
            @Parameter(hidden = true) @PageableDefault(sort = "fullName") Pageable pageable);
    
    @GetMapping("/search")
    @Operation(
        summary = "Search staff members",
        description = "Search staff members by name, email, or phone number.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: fullName", example = "fullName")
        }
    )
    @ApiResponse(responseCode = "200", description = "Staff members retrieved")
    ResponseEntity<Page<StaffDto>> searchStaff(
            @Parameter(name = "searchTerm", description = "Search term for filtering staff members")
            @RequestParam(required = false) String searchTerm,
            @Parameter(hidden = true) @PageableDefault(sort = "fullName") Pageable pageable);
    
    @PostMapping("/search/advanced")
    @Operation(
        summary = "Advanced staff search",
        description = "Search staff members with multiple criteria and filters.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: fullName", example = "fullName")
        }
    )
    @ApiResponse(responseCode = "200", description = "Staff members retrieved")
    ResponseEntity<Page<StaffDto>> advancedSearchStaff(
            @Valid @RequestBody StaffSearchCriteria criteria,
            @Parameter(hidden = true) @PageableDefault(sort = "fullName") Pageable pageable);
    
    @PostMapping
    @Operation(
        summary = "Create new staff member",
        description = "Creates a new staff member record."
    )
    @ApiResponse(responseCode = "201", description = "Staff member created",
                content = @Content(schema = @Schema(implementation = StaffDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or email already exists")
    ResponseEntity<StaffDto> createStaff(
            @Valid @RequestBody StaffCreateRequest request);
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update staff member",
        description = "Updates an existing staff member record."
    )
    @ApiResponse(responseCode = "200", description = "Staff member updated",
                content = @Content(schema = @Schema(implementation = StaffDto.class)))
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    @ApiResponse(responseCode = "400", description = "Validation error or email already exists")
    ResponseEntity<StaffDto> updateStaff(
            @Parameter(description = "Staff ID") @PathVariable UUID id,
            @Valid @RequestBody StaffUpdateRequest request);
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete staff member",
        description = "Deactivates a staff member (soft delete)."
    )
    @ApiResponse(responseCode = "204", description = "Staff member deactivated")
    @ApiResponse(responseCode = "404", description = "Staff member not found")
    ResponseEntity<Void> deleteStaff(
            @Parameter(description = "Staff ID") @PathVariable UUID id);
}
