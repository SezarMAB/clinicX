package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_allocations")
@Getter
@Setter
public class PaymentAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @NotNull
    @Column(name = "allocated_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal allocatedAmount;

    @CreationTimestamp
    @Column(name = "allocated_at", updatable = false, nullable = false)
    private Instant allocatedAt;
}


