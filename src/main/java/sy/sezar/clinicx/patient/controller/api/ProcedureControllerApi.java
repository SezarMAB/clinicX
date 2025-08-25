package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.ProcedureSummaryDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/procedures")
@Tag(name = "Procedures", description = "Operations related to dental procedure management")
public interface ProcedureControllerApi {

    @GetMapping
    @Operation(
        summary = "Get all procedures",
        description = "Retrieves paginated list of all available dental procedures.",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "page", description = "Zero-based page index (0..N)", example = "0"),
            @io.swagger.v3.oas.annotations.Parameter(name = "size", description = "The size of the page to be returned", example = "20"),
            @io.swagger.v3.oas.annotations.Parameter(name = "sort", description = "Sorting criteria: property(,asc|desc). Default: name", example = "name")
        }
    )
    @ApiResponse(responseCode = "200", description = "Procedures retrieved")
    ResponseEntity<Page<ProcedureSummaryDto>> getAllProcedures(
            @Parameter(hidden = true) @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable);

    @GetMapping("/active")
    @Operation(
        summary = "Get active procedures",
        description = "Retrieves list of all active dental procedures."
    )
    @ApiResponse(responseCode = "200", description = "Active procedures retrieved")
    ResponseEntity<List<ProcedureSummaryDto>> getActiveProcedures();

    @GetMapping("/{id}")
    @Operation(
        summary = "Get procedure by ID",
        description = "Retrieves a specific procedure by its UUID."
    )
    @ApiResponse(responseCode = "200", description = "Procedure found",
                content = @Content(schema = @Schema(implementation = ProcedureSummaryDto.class)))
    @ApiResponse(responseCode = "404", description = "Procedure not found")
    ResponseEntity<ProcedureSummaryDto> getProcedureById(
            @Parameter(name = "id", description = "Procedure UUID", required = true)
            @PathVariable UUID id);

    @GetMapping("/search")
    @Operation(
        summary = "Search procedures",
        description = "Searches procedures by name or description."
    )
    @ApiResponse(responseCode = "200", description = "Search results retrieved")
    ResponseEntity<List<ProcedureSummaryDto>> searchProcedures(
            @Parameter(name = "searchTerm", description = "Search term for procedure name or description")
            @RequestParam(required = false) String searchTerm);
}