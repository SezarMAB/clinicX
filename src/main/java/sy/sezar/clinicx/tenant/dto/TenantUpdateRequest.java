package sy.sezar.clinicx.tenant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record TenantUpdateRequest(
    @Size(min = 3, max = 100, message = "Tenant name must be between 3 and 100 characters")
    String name,
    
    @Email(message = "Invalid email format")
    String contactEmail,
    
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
    String contactPhone,
    
    String address,
    
    String subscriptionPlan,
    
    Instant subscriptionEndDate,
    
    Integer maxUsers,
    
    Integer maxPatients
) {}