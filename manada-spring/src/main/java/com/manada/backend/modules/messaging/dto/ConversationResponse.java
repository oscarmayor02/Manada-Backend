package com.manada.backend.modules.messaging.dto;

import com.manada.backend.modules.messaging.Conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversationResponse(
    UUID id, String contextType, String contextRef, Instant createdAt,
    List<UUID> participantIds, String lastMessageText
) {
    public static ConversationResponse from(Conversation c, List<UUID> participantIds, String lastMessageText) {
        return new ConversationResponse(c.getId(), c.getContextType(), c.getContextRef(), c.getCreatedAt(), participantIds, lastMessageText);
    }
}
