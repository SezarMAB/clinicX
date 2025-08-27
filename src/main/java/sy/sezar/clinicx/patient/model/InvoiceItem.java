package sy.sezar.clinicx.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import sy.sezar.clinicx.patient.model.enums.InvoiceItemType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", unique = true)
    private Visit visit;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", length = 30)
    private InvoiceItemType itemType = InvoiceItemType.PROCEDURE;

    @NotNull
    @Size(max = 255)
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;
}

