package com.manada.backend.modules.community;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "post_like", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
