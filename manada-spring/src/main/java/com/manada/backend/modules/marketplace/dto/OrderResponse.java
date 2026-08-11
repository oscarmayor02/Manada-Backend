package com.manada.backend.modules.marketplace.dto;

import com.manada.backend.modules.marketplace.OrderItem;
import com.manada.backend.modules.marketplace.OrderStatus;
import com.manada.backend.modules.marketplace.PurchaseOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id, UUID buyerId, OrderStatus status, BigDecimal subtotal,
    BigDecimal commissionAmt, BigDecimal total, List<OrderItem> items
) {
    public static OrderResponse from(PurchaseOrder o, List<OrderItem> items) {
        return new OrderResponse(o.getId(), o.getBuyerId(), o.getStatus(), o.getSubtotal(),
            o.getCommissionAmt(), o.getTotal(), items);
    }
}
