package com.manada.backend.modules.marketplace;

import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.marketplace.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    public MarketplaceController(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    @GetMapping("/products")
    public List<ProductResponse> listProducts(@RequestParam(required = false) ProductCategory category) {
        return marketplaceService.listProducts(category);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody ProductRequest req) {
        return marketplaceService.createProduct(user.id(), req);
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody CreateOrderRequest req) {
        return marketplaceService.createOrder(user.id(), req);
    }
}
