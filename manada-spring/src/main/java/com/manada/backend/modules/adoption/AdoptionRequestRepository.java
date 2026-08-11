package com.manada.backend.modules.adoption;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, UUID> {
}
