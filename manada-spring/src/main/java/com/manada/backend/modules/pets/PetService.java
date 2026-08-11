package com.manada.backend.modules.pets;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.modules.pets.dto.PetRequest;
import com.manada.backend.modules.pets.dto.PetResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<PetResponse> listMine(UUID ownerId) {
        return petRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream().map(PetResponse::from).toList();
    }

    public PetResponse create(UUID ownerId, PetRequest req) {
        Pet pet = new Pet();
        pet.setOwnerId(ownerId);
        applyRequest(pet, req);
        return PetResponse.from(petRepository.save(pet));
    }

    public PetResponse getOne(UUID id) {
        return PetResponse.from(findOwned(id, null, false));
    }

    public PetResponse update(UUID id, UUID ownerId, PetRequest req) {
        Pet pet = findOwned(id, ownerId, true);
        applyRequest(pet, req);
        return PetResponse.from(petRepository.save(pet));
    }

    public void delete(UUID id, UUID ownerId) {
        Pet pet = findOwned(id, ownerId, true);
        petRepository.delete(pet);
    }

    private void applyRequest(Pet pet, PetRequest req) {
        pet.setName(req.name());
        pet.setSpecies(req.species());
        pet.setSex(req.sex());
        pet.setSize(req.size());
        pet.setBreed(req.breed());
        pet.setBirthYear(req.birthYear());
        pet.setPhotoUrl(req.photoUrl());
    }

    /** Busca la mascota y, si se pide, valida que pertenezca al dueño autenticado. */
    private Pet findOwned(UUID id, UUID ownerId, boolean checkOwnership) {
        Pet pet = petRepository.findById(id).orElseThrow(() -> ApiException.notFound("Mascota no encontrada."));
        if (checkOwnership && !pet.getOwnerId().equals(ownerId)) {
            throw ApiException.forbidden("No es tu mascota.");
        }
        return pet;
    }
}
