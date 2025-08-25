package sy.sezar.clinicx.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sy.sezar.clinicx.patient.model.PaymentAllocation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, UUID> {
    List<PaymentAllocation> findByInvoiceId(UUID invoiceId);
    List<PaymentAllocation> findByPaymentId(UUID paymentId);

    @Query("select coalesce(sum(pa.allocatedAmount), 0) from PaymentAllocation pa where pa.invoice.id = :invoiceId")
    BigDecimal sumAllocatedAmountByInvoiceId(@Param("invoiceId") UUID invoiceId);
}


