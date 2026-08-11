package com.manada.backend.modules.sos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SightingRepository extends JpaRepository<Sighting, UUID> {
}
