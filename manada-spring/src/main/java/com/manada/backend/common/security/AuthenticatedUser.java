package com.manada.backend.common.security;

import java.util.UUID;

/** Representa al usuario autenticado extraído del JWT en el request actual. */
public record AuthenticatedUser(UUID id, String email, String accountType) {
}
