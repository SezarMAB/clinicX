package sy.sezar.clinicx.patient.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.patient.dto.LedgerEntryDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ledger")
@Tag(name = "Ledger", description = "Patient unified financial ledger")
public interface LedgerControllerApi {

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Get patient ledger entries",
        description = "Retrieves paginated ledger entries with chronological order."
    )
    @ApiResponse(responseCode = "200", description = "Ledger entries retrieved successfully")
    ResponseEntity<Page<LedgerEntryDto>> getPatientLedger(
            @Parameter(description = "Patient UUID", required = true)
            @PathVariable UUID patientId,
            @Parameter(hidden = true) @PageableDefault(sort = "occurredAt", direction = Sort.Direction.ASC) Pageable pageable);
}


