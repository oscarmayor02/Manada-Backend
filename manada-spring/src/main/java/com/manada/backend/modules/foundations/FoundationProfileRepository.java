package com.manada.backend.modules.foundations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FoundationProfileRepository extends JpaRepository<FoundationProfile, UUID> {
    Optional<FoundationProfile> findByUserId(UUID userId);
}
