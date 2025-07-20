package sy.sezar.clinicx.clinic.controller.api;

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
import sy.sezar.clinicx.clinic.dto.SpecialtyCreateRequest;
import sy.sezar.clinicx.clinic.dto.SpecialtyDto;
import sy.sezar.clinicx.clinic.dto.SpecialtyUpdateRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/specialties")
@Tag(name = "Specialties", description = "Operations related to specialty management")
public interface SpecialtyControllerApi {
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get specialty by ID",
        description = "Retrieves a specialty by its unique identifier."
    )
    @ApiResponse(responseCode = "200", description = "Specialty found",
                content = @Content(schema = @Schema(implementation = SpecialtyDto.class)))
    @ApiResponse(responseCode = "404", description = "Specialty not found")
    ResponseEntity<SpecialtyDto> getSpecialtyById(
            @Parameter(description = "Specialty ID") @PathVariable UUID id);
    
    @GetMapping
    @Operation(
        summary = "Get all specialties",
        description = "Retrieves paginated list of all specialties.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: name", example = "name")
        }
    )
    @ApiResponse(responseCode = "200", description = "Specialties retrieved")
    ResponseEntity<Page<SpecialtyDto>> getAllSpecialties(
            @Parameter(hidden = true) @PageableDefault(sort = "name") Pageable pageable);
    
    @GetMapping("/active")
    @Operation(
        summary = "Get active specialties",
        description = "Retrieves paginated list of active specialties.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: name", example = "name")
        }
    )
    @ApiResponse(responseCode = "200", description = "Active specialties retrieved")
    ResponseEntity<Page<SpecialtyDto>> getActiveSpecialties(
            @Parameter(hidden = true) @PageableDefault(sort = "name") Pageable pageable);
    
    @GetMapping("/search")
    @Operation(
        summary = "Search specialties",
        description = "Search specialties by name or description.",
        parameters = {
            @Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: name", example = "name")
        }
    )
    @ApiResponse(responseCode = "200", description = "Specialties retrieved")
    ResponseEntity<Page<SpecialtyDto>> searchSpecialties(
            @Parameter(name = "searchTerm", description = "Search term for filtering specialties")
            @RequestParam(required = false) String searchTerm,
            @Parameter(hidden = true) @PageableDefault(sort = "name") Pageable pageable);
    
    @PostMapping
    @Operation(
        summary = "Create new specialty",
        description = "Creates a new specialty record."
    )
    @ApiResponse(responseCode = "201", description = "Specialty created",
                content = @Content(schema = @Schema(implementation = SpecialtyDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or specialty already exists")
    ResponseEntity<SpecialtyDto> createSpecialty(
            @Valid @RequestBody SpecialtyCreateRequest request);
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Update specialty",
        description = "Updates an existing specialty record."
    )
    @ApiResponse(responseCode = "200", description = "Specialty updated",
                content = @Content(schema = @Schema(implementation = SpecialtyDto.class)))
    @ApiResponse(responseCode = "404", description = "Specialty not found")
    @ApiResponse(responseCode = "400", description = "Validation error or specialty name already exists")
    ResponseEntity<SpecialtyDto> updateSpecialty(
            @Parameter(description = "Specialty ID") @PathVariable UUID id,
            @Valid @RequestBody SpecialtyUpdateRequest request);
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete specialty",
        description = "Deactivates a specialty (soft delete)."
    )
    @ApiResponse(responseCode = "204", description = "Specialty deactivated")
    @ApiResponse(responseCode = "404", description = "Specialty not found")
    ResponseEntity<Void> deleteSpecialty(
            @Parameter(description = "Specialty ID") @PathVariable UUID id);
}
