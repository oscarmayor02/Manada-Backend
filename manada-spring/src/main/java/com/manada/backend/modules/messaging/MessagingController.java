package com.manada.backend.modules.messaging;

import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.messaging.dto.ConversationResponse;
import com.manada.backend.modules.messaging.dto.SendMessageRequest;
import com.manada.backend.modules.messaging.dto.StartConversationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessagingController {

    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping("/conversations")
    public List<ConversationResponse> listMine(@AuthenticationPrincipal AuthenticatedUser user) {
        return messagingService.listMine(user.id());
    }

    @PostMapping("/conversations")
    @ResponseStatus(HttpStatus.CREATED)
    public Conversation startConversation(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody StartConversationRequest req) {
        return messagingService.startConversation(user.id(), req);
    }

    @GetMapping("/conversations/{id}")
    public List<Message> getMessages(@PathVariable UUID id) {
        return messagingService.getMessages(id);
    }

    @PostMapping("/conversations/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Message sendMessage(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable UUID id, @Valid @RequestBody SendMessageRequest req) {
        return messagingService.sendMessage(id, user.id(), req);
    }
}
