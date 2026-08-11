package com.manada.backend.modules.messaging.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StartConversationRequest(
    @NotNull UUID otherUserId,
    String contextType,
    String contextRef,
    String firstMessage
) {}
