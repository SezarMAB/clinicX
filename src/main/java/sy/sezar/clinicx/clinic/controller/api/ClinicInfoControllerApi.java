package sy.sezar.clinicx.clinic.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sy.sezar.clinicx.clinic.dto.ClinicInfoDto;
import sy.sezar.clinicx.clinic.dto.ClinicInfoUpdateRequest;

@RestController
@RequestMapping("/api/v1/clinic-info")
@Tag(name = "Clinic Info", description = "Operations related to clinic information management")
public interface ClinicInfoControllerApi {
    
    @GetMapping
    @Operation(
        summary = "Get clinic information",
        description = "Retrieves the clinic information."
    )
    @ApiResponse(responseCode = "200", description = "Clinic information retrieved",
                content = @Content(schema = @Schema(implementation = ClinicInfoDto.class)))
    @ApiResponse(responseCode = "404", description = "Clinic information not found")
    ResponseEntity<ClinicInfoDto> getClinicInfo();
    
    @PutMapping
    @Operation(
        summary = "Update clinic information",
        description = "Updates the clinic information."
    )
    @ApiResponse(responseCode = "200", description = "Clinic information updated",
                content = @Content(schema = @Schema(implementation = ClinicInfoDto.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Clinic information not found")
    ResponseEntity<ClinicInfoDto> updateClinicInfo(
            @Valid @RequestBody ClinicInfoUpdateRequest request);
}
