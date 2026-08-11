package com.manada.backend.modules.sos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LostPetAlertRepository extends JpaRepository<LostPetAlert, UUID> {
    List<LostPetAlert> findByStatusInOrderByCreatedAtDesc(List<AlertStatus> statuses);
}
