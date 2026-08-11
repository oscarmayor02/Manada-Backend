package com.manada.backend.modules.sos;

import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.sos.dto.AlertResponse;
import com.manada.backend.modules.sos.dto.CreateAlertRequest;
import com.manada.backend.modules.sos.dto.SightingRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sos")
public class SosController {

    private final SosService sosService;

    public SosController(SosService sosService) {
        this.sosService = sosService;
    }

    @GetMapping
    public List<AlertResponse> listActive() {
        return sosService.listActive();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody CreateAlertRequest req) {
        var result = sosService.create(user.id(), req);
        return Map.of("alert", result.alert(), "notified", result.estimatedReach());
    }

    @PostMapping("/{id}/sightings")
    @ResponseStatus(HttpStatus.CREATED)
    public Sighting reportSighting(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id, @RequestBody SightingRequest req) {
        return sosService.reportSighting(id, user.id(), req);
    }

    @PatchMapping("/{id}/resolve")
    public AlertResponse resolve(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        return sosService.resolve(id, user.id());
    }
}
