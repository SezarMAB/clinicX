package sy.sezar.clinicx.patient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sy.sezar.clinicx.patient.model.enums.PaymentType;
import sy.sezar.clinicx.patient.model.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Individual payment item for bulk payment processing.
 */
public record BulkPaymentItem(
    @NotNull(message = "Patient ID is required")
    UUID patientId,
    
    UUID invoiceId,
    
    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be positive")
    BigDecimal amount,
    
    @NotNull(message = "Payment date is required")
    LocalDate paymentDate,
    
    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,
    
    @NotNull(message = "Payment type is required")
    PaymentType type,
    
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description,
    
    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    String referenceNumber
) {}