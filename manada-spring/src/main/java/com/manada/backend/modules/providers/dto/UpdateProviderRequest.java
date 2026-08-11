package com.manada.backend.modules.providers.dto;

public record UpdateProviderRequest(String businessName, String taxId, String city, String documentsUrl) {}
