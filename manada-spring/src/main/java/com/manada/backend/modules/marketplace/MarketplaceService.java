package com.manada.backend.modules.marketplace;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.modules.marketplace.dto.*;
import com.manada.backend.modules.providers.ProviderProfile;
import com.manada.backend.modules.providers.ProviderProfileRepository;
import com.manada.backend.modules.providers.ProviderVerificationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MarketplaceService {

    private final ProductRepository productRepository;
    private final PurchaseOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProviderProfileRepository providerRepository;

    public MarketplaceService(
            ProductRepository productRepository,
            PurchaseOrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProviderProfileRepository providerRepository
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.providerRepository = providerRepository;
    }

    public List<ProductResponse> listProducts(ProductCategory category) {
        List<Product> products = category != null
            ? productRepository.findByActiveTrueAndCategory(category)
            : productRepository.findByActiveTrue();
        return products.stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse createProduct(UUID userId, ProductRequest req) {
        ProviderProfile provider = providerRepository.findByUserId(userId)
            .orElseThrow(() -> ApiException.forbidden("Solo proveedores pueden publicar productos."));
        if (provider.getVerificationStatus() != ProviderVerificationStatus.VERIFICADO) {
            throw ApiException.forbidden("Tu cuenta debe estar verificada para publicar productos.");
        }

        Product product = new Product();
        product.setProviderId(provider.getId());
        product.setName(req.name());
        product.setCategory(req.category());
        product.setPrice(req.price());
        product.setStock(req.stock());
        product.setPhotoUrl(req.photoUrl());

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public OrderResponse createOrder(UUID buyerId, CreateOrderRequest req) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal commissionAmt = BigDecimal.ZERO;
        List<OrderItem> lineItems = new ArrayList<>();

        // primero calculamos todo (y validamos que los productos existan) antes de guardar nada
        record Line(Product product, int quantity, BigDecimal lineTotal) {}
        List<Line> lines = new ArrayList<>();

        for (var itemReq : req.items()) {
            Product product = productRepository.findById(itemReq.productId())
                .orElseThrow(() -> ApiException.badRequest("Alguno de los productos no existe."));
            ProviderProfile provider = providerRepository.findById(product.getProviderId())
                .orElseThrow(() -> ApiException.badRequest("Proveedor del producto no encontrado."));

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            subtotal = subtotal.add(lineTotal);
            // La comisión de Manada se calcula según la tasa configurada por proveedor
            // (ProviderProfile.commissionRate) — así cada proveedor puede tener % distinto.
            commissionAmt = commissionAmt.add(lineTotal.multiply(provider.getCommissionRate()));

            lines.add(new Line(product, itemReq.quantity(), lineTotal));
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setBuyerId(buyerId);
        order.setSubtotal(subtotal);
        order.setCommissionAmt(commissionAmt);
        order.setTotal(subtotal); // el comprador paga el subtotal; la comisión se descuenta al proveedor, no se le suma al comprador
        order = orderRepository.save(order);

        for (Line line : lines) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(line.product().getId());
            item.setQuantity(line.quantity());
            item.setUnitPrice(line.product().getPrice());
            lineItems.add(orderItemRepository.save(item));
        }

        // ------------------------------------------------------------------
        // PUNTO DE INTEGRACIÓN DE PAGOS (Wompi / Mercado Pago):
        // aquí se crea la transacción con split payment: order.getCommissionAmt()
        // se queda en la cuenta de Manada, el resto se transfiere al proveedor.
        // ------------------------------------------------------------------

        return OrderResponse.from(order, lineItems);
    }
}
