package com.manada.backend.modules.providers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, UUID> {
    Optional<ProviderProfile> findByUserId(UUID userId);
    List<ProviderProfile> findByVerificationStatusAndProviderType(ProviderVerificationStatus status, ProviderType type);
    List<ProviderProfile> findByVerificationStatus(ProviderVerificationStatus status);
}
