package com.manada.backend.modules.messaging;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversation")
@Getter
@Setter
@NoArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "context_type")
    private String contextType; // ej. "sos", "adopcion"

    @Column(name = "context_ref")
    private String contextRef; // ej. "Toby · Perdido"

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
