package com.manada.backend.modules.marketplace;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@NoArgsConstructor
public class PurchaseOrder {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDIENTE;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(name = "commission_amt", nullable = false)
    private BigDecimal commissionAmt;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

}
