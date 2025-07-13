package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.FinancialRecordDto;
import sy.sezar.clinicx.patient.dto.PaymentInstallmentDto;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.Payment;

import java.util.List;

/**
 * Mapper for converting between Invoice/Payment entities and their DTOs.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    /**
     * Maps Invoice entity to FinancialRecordDto.
     */
    @Mapping(target = "recordId", source = "id")
    @Mapping(target = "installments", source = "payments")
    FinancialRecordDto toFinancialRecordDto(Invoice invoice);

    /**
     * Maps list of Invoices to list of FinancialRecordDtos.
     */
    List<FinancialRecordDto> toFinancialRecordDtoList(List<Invoice> invoices);

    /**
     * Maps Payment entity to PaymentInstallmentDto.
     */
    @Mapping(target = "description", source = "paymentMethod")
    PaymentInstallmentDto toPaymentInstallmentDto(Payment payment);

    /**
     * Maps list of Payments to list of PaymentInstallmentDtos.
     */
    List<PaymentInstallmentDto> toPaymentInstallmentDtoList(List<Payment> payments);
}
