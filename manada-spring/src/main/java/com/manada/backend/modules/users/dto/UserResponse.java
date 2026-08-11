package com.manada.backend.modules.users.dto;

import com.manada.backend.modules.users.AccountType;
import com.manada.backend.modules.users.User;

import java.time.Instant;
import java.util.UUID;

// Nunca incluye el passwordHash: es la forma segura de devolver el usuario al cliente.
public record UserResponse(
    UUID id,
    String email,
    String fullName,
    String phone,
    AccountType accountType,
    Instant createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(), u.getAccountType(), u.getCreatedAt());
    }
}
