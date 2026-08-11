package com.manada.backend.modules.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("""
        SELECT c FROM Conversation c
        WHERE c.id IN (SELECT cp.conversationId FROM ConversationParticipant cp WHERE cp.userId = :userId)
        ORDER BY c.createdAt DESC
        """)
    List<Conversation> findAllForUser(@Param("userId") UUID userId);

    @Query("""
        SELECT c FROM Conversation c
        WHERE c.contextRef = :contextRef
        AND c.id IN (SELECT cp.conversationId FROM ConversationParticipant cp WHERE cp.userId = :userA)
        AND c.id IN (SELECT cp.conversationId FROM ConversationParticipant cp WHERE cp.userId = :userB)
        """)
    Optional<Conversation> findExisting(@Param("userA") UUID userA, @Param("userB") UUID userB, @Param("contextRef") String contextRef);
}
