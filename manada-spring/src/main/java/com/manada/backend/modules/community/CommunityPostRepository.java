package com.manada.backend.modules.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, UUID> {
    List<CommunityPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
