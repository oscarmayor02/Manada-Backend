package com.manada.backend.modules.community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    long countByPostId(UUID postId);
    Optional<Like> findByPostIdAndUserId(UUID postId, UUID userId);
}
