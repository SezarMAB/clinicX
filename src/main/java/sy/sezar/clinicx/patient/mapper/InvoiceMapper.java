package sy.sezar.clinicx.patient.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sy.sezar.clinicx.patient.dto.FinancialRecordDto;
import sy.sezar.clinicx.patient.dto.PaymentInstallmentDto;
import sy.sezar.clinicx.patient.model.Invoice;
import sy.sezar.clinicx.patient.model.Payment;

import java.util.List;

/**
 * Mapper for financial entities like Invoice and Payment to their DTOs.
 */
@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "id", target = "recordId")
    @Mapping(source = "invoiceNumber", target = "invoiceNumber")
    @Mapping(source = "issueDate", target = "issueDate")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "totalAmount", target = "amount")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "installments", source = "payments")
    FinancialRecordDto toFinancialRecordDto(Invoice invoice);

    List<FinancialRecordDto> toFinancialRecordDtos(List<Invoice> invoices);

    @Mapping(source = "paymentDate", target = "paymentDate")
    @Mapping(source = "amount", target = "amount")
    @Mapping(target = "description", constant = "Payment")
    PaymentInstallmentDto toPaymentInstallmentDto(Payment payment);

    List<PaymentInstallmentDto> toPaymentInstallmentDtos(List<Payment> payments);
}
