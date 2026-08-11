package com.manada.backend.modules.messaging;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.modules.messaging.dto.ConversationResponse;
import com.manada.backend.modules.messaging.dto.SendMessageRequest;
import com.manada.backend.modules.messaging.dto.StartConversationRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MessagingService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;

    public MessagingService(
            ConversationRepository conversationRepository,
            ConversationParticipantRepository participantRepository,
            MessageRepository messageRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.messageRepository = messageRepository;
    }

    public List<ConversationResponse> listMine(UUID userId) {
        return conversationRepository.findAllForUser(userId).stream().map(c -> {
            List<UUID> participantIds = participantRepository.findByConversationId(c.getId()).stream()
                .map(ConversationParticipant::getUserId).toList();
            String lastMessage = messageRepository.findFirstByConversationIdOrderByCreatedAtDesc(c.getId())
                .map(Message::getText).orElse(null);
            return ConversationResponse.from(c, participantIds, lastMessage);
        }).toList();
    }

    @Transactional
    public Conversation startConversation(UUID userId, StartConversationRequest req) {
        Conversation conversation = conversationRepository
            .findExisting(userId, req.otherUserId(), req.contextRef())
            .orElseGet(() -> {
                Conversation c = new Conversation();
                c.setContextType(req.contextType());
                c.setContextRef(req.contextRef());
                c = conversationRepository.save(c);

                ConversationParticipant p1 = new ConversationParticipant();
                p1.setConversationId(c.getId());
                p1.setUserId(userId);
                participantRepository.save(p1);

                ConversationParticipant p2 = new ConversationParticipant();
                p2.setConversationId(c.getId());
                p2.setUserId(req.otherUserId());
                participantRepository.save(p2);

                return c;
            });

        if (req.firstMessage() != null && !req.firstMessage().isBlank()) {
            Message message = new Message();
            message.setConversationId(conversation.getId());
            message.setSenderId(userId);
            message.setText(req.firstMessage());
            messageRepository.save(message);
        }

        return conversation;
    }

    public List<Message> getMessages(UUID conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw ApiException.notFound("Conversación no encontrada.");
        }
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public Message sendMessage(UUID conversationId, UUID senderId, SendMessageRequest req) {
        if (!conversationRepository.existsById(conversationId)) {
            throw ApiException.notFound("Conversación no encontrada.");
        }
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setText(req.text());
        return messageRepository.save(message);
    }
}
