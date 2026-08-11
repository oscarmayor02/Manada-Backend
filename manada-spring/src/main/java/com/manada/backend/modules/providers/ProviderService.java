package com.manada.backend.modules.providers;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.modules.providers.dto.ProviderResponse;
import com.manada.backend.modules.providers.dto.UpdateProviderRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProviderService {

    private final ProviderProfileRepository repository;

    public ProviderService(ProviderProfileRepository repository) {
        this.repository = repository;
    }

    public List<ProviderResponse> list(ProviderType type) {
        List<ProviderProfile> providers = type != null
            ? repository.findByVerificationStatusAndProviderType(ProviderVerificationStatus.VERIFICADO, type)
            : repository.findByVerificationStatus(ProviderVerificationStatus.VERIFICADO);
        return providers.stream().map(ProviderResponse::from).toList();
    }

    public ProviderResponse me(UUID userId) {
        return ProviderResponse.from(findByUserId(userId));
    }

    public ProviderResponse update(UUID userId, UpdateProviderRequest req) {
        ProviderProfile p = findByUserId(userId);
        if (req.businessName() != null) p.setBusinessName(req.businessName());
        if (req.taxId() != null) p.setTaxId(req.taxId());
        if (req.city() != null) p.setCity(req.city());
        if (req.documentsUrl() != null) {
            p.setDocumentsUrl(req.documentsUrl());
            // subir documentos nuevos vuelve a poner la cuenta en revisión
            p.setVerificationStatus(ProviderVerificationStatus.DOCUMENTOS_EN_REVISION);
        }
        return ProviderResponse.from(repository.save(p));
    }

    /**
     * ADMIN — aprobar/rechazar/suspender un proveedor.
     * NOTA: falta protegerlo con un rol ADMIN real (hoy cualquier usuario autenticado
     * podría llamarlo). Cuando tengas roles, agrega @PreAuthorize("hasRole('ADMIN')").
     */
    public ProviderResponse verify(UUID providerId, String status) {
        ProviderProfile p = repository.findById(providerId)
            .orElseThrow(() -> ApiException.notFound("Proveedor no encontrado."));
        try {
            p.setVerificationStatus(ProviderVerificationStatus.valueOf(status));
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest("status inválido.");
        }
        return ProviderResponse.from(repository.save(p));
    }

    private ProviderProfile findByUserId(UUID userId) {
        return repository.findByUserId(userId)
            .orElseThrow(() -> ApiException.notFound("No tienes un perfil de proveedor."));
    }
}
