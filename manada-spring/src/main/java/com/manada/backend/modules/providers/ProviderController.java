package com.manada.backend.modules.providers;

import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.providers.dto.ProviderResponse;
import com.manada.backend.modules.providers.dto.UpdateProviderRequest;
import com.manada.backend.modules.providers.dto.VerifyProviderRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping
    public List<ProviderResponse> list(@RequestParam(required = false) ProviderType type) {
        return providerService.list(type);
    }

    @GetMapping("/me")
    public ProviderResponse me(@AuthenticationPrincipal AuthenticatedUser user) {
        return providerService.me(user.id());
    }

    @PatchMapping("/me")
    public ProviderResponse update(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody UpdateProviderRequest req) {
        return providerService.update(user.id(), req);
    }

    @PatchMapping("/{id}/verify")
    public ProviderResponse verify(@PathVariable UUID id, @RequestBody VerifyProviderRequest req) {
        return providerService.verify(id, req.status());
    }
}
