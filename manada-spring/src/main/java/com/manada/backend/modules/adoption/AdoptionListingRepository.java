package com.manada.backend.modules.adoption;

import com.manada.backend.modules.pets.Species;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AdoptionListingRepository extends JpaRepository<AdoptionListing, UUID> {
    List<AdoptionListing> findByStatusAndSpecies(AdoptionStatus status, Species species);
    List<AdoptionListing> findByStatus(AdoptionStatus status);
}
