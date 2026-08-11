package com.manada.backend.modules.users.dto;

public record AuthResponse(String token, UserResponse user) {}
