package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sy.sezar.clinicx.patient.model.enums.PaymentType;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for updating an existing payment.
 */
public record PaymentUpdateRequest(
    UUID invoiceId,
    
    @Positive(message = "Payment amount must be positive")
    BigDecimal amount,
    
    LocalDate paymentDate,
    
    PaymentMethod paymentMethod,
    
    PaymentType type,
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description,
    
    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    String referenceNumber
) {}