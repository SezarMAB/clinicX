package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sy.sezar.clinicx.patient.dto.PaymentCreateRequest;
import sy.sezar.clinicx.patient.dto.PaymentDto;
import sy.sezar.clinicx.patient.dto.RefundRequest;
import sy.sezar.clinicx.patient.model.Payment;

/**
 * Mapper for converting between Payment entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    /**
     * Convert Payment entity to PaymentDto.
     *
     * @param payment Payment entity
     * @return PaymentDto
     */
    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(source = "patient.fullName", target = "patientName")
    @Mapping(source = "invoice.id", target = "invoiceId")
    @Mapping(source = "createdBy.email", target = "createdBy")
    PaymentDto toDto(Payment payment);

    /**
     * Convert PaymentCreateRequest to Payment entity.
     *
     * @param request PaymentCreateRequest
     * @return Payment entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(source = "notes", target = "description")
    Payment toEntity(PaymentCreateRequest request);

    /**
     * Convert RefundRequest to Payment entity.
     *
     * @param request RefundRequest
     * @return Payment entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(source = "reason", target = "description")
    @Mapping(source = "refundDate", target = "paymentDate")
    Payment toRefundEntity(RefundRequest request);

    /**
     * Update Payment entity from PaymentCreateRequest.
     *
     * @param request PaymentCreateRequest
     * @param payment Payment entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "invoice", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(source = "notes", target = "description")
    void updatePaymentFromRequest(PaymentCreateRequest request, @MappingTarget Payment payment);
}