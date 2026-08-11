package com.manada.backend.modules.pets;

import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.pets.dto.PetRequest;
import com.manada.backend.modules.pets.dto.PetResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public List<PetResponse> listMine(@AuthenticationPrincipal AuthenticatedUser user) {
        return petService.listMine(user.id());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PetResponse create(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody PetRequest req) {
        return petService.create(user.id(), req);
    }

    @GetMapping("/{id}")
    public PetResponse getOne(@PathVariable UUID id) {
        return petService.getOne(id);
    }

    @PutMapping("/{id}")
    public PetResponse update(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id, @Valid @RequestBody PetRequest req) {
        return petService.update(id, user.id(), req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id) {
        petService.delete(id, user.id());
    }
}
