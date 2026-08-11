package com.manada.backend.modules.adoption;

import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.adoption.dto.*;
import com.manada.backend.modules.pets.Species;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/adoption")
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @GetMapping
    public List<AdoptionListingResponse> list(@RequestParam(required = false) Species species) {
        return adoptionService.list(species);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionListingResponse create(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody AdoptionListingRequest req) {
        return adoptionService.create(user.id(), req);
    }

    @PostMapping("/{id}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionRequest requestAdoption(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id, @RequestBody AdoptionRequestDto req) {
        return adoptionService.requestAdoption(id, user.id(), req);
    }

    @PatchMapping("/requests/{id}")
    public AdoptionRequest updateStatus(@PathVariable UUID id, @Valid @RequestBody UpdateRequestStatusDto req) {
        return adoptionService.updateRequestStatus(id, req.status());
    }
}
