package com.manada.backend.modules.marketplace;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue
    private UUID id;

    // Nota: no se mapea como relación @ManyToOne a propósito, para mantener
    // el módulo de marketplace simple; si prefieres navegación por objeto,
    // se puede cambiar a @ManyToOne con @JoinColumn(name = "order_id").
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
}
