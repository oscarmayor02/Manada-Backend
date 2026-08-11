package com.manada.backend.modules.adoption;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.modules.adoption.dto.*;
import com.manada.backend.modules.foundations.FoundationProfile;
import com.manada.backend.modules.foundations.FoundationProfileRepository;
import com.manada.backend.modules.foundations.VerificationStatus;
import com.manada.backend.modules.notifications.NotificationService;
import com.manada.backend.modules.notifications.NotificationType;
import com.manada.backend.modules.pets.Species;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdoptionService {

    private final AdoptionListingRepository listingRepository;
    private final AdoptionRequestRepository requestRepository;
    private final FoundationProfileRepository foundationRepository;
    private final NotificationService notificationService;

    public AdoptionService(
            AdoptionListingRepository listingRepository,
            AdoptionRequestRepository requestRepository,
            FoundationProfileRepository foundationRepository,
            NotificationService notificationService
    ) {
        this.listingRepository = listingRepository;
        this.requestRepository = requestRepository;
        this.foundationRepository = foundationRepository;
        this.notificationService = notificationService;
    }

    public List<AdoptionListingResponse> list(Species species) {
        List<AdoptionListing> listings = species != null
            ? listingRepository.findByStatusAndSpecies(AdoptionStatus.DISPONIBLE, species)
            : listingRepository.findByStatus(AdoptionStatus.DISPONIBLE);
        return listings.stream().map(AdoptionListingResponse::from).toList();
    }

    @Transactional
    public AdoptionListingResponse create(UUID userId, AdoptionListingRequest req) {
        FoundationProfile foundation = foundationRepository.findByUserId(userId)
            .orElseThrow(() -> ApiException.forbidden("Solo fundaciones pueden publicar animales en adopción."));
        if (foundation.getVerificationStatus() != VerificationStatus.VERIFICADO) {
            throw ApiException.forbidden("Tu fundación debe estar verificada para publicar.");
        }

        AdoptionListing listing = new AdoptionListing();
        listing.setFoundationId(foundation.getId());
        listing.setName(req.name());
        listing.setSpecies(req.species());
        listing.setSex(req.sex());
        listing.setSize(req.size());
        listing.setAgeLabel(req.ageLabel());
        listing.setDescription(req.description());
        listing.setPhotoUrl(req.photoUrl());

        return AdoptionListingResponse.from(listingRepository.save(listing));
    }

    @Transactional
    public AdoptionRequest requestAdoption(UUID listingId, UUID applicantId, AdoptionRequestDto req) {
        AdoptionListing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> ApiException.notFound("Publicación no encontrada."));

        AdoptionRequest request = new AdoptionRequest();
        request.setListingId(listing.getId());
        request.setApplicantId(applicantId);
        request.setMessage(req.message());
        return requestRepository.save(request);
    }

    @Transactional
    public AdoptionRequest updateRequestStatus(UUID requestId, String statusStr) {
        AdoptionRequest request = requestRepository.findById(requestId)
            .orElseThrow(() -> ApiException.notFound("Solicitud no encontrada."));

        AdoptionRequestStatus status;
        try {
            status = AdoptionRequestStatus.valueOf(statusStr);
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest("status debe ser uno de: EN_REVISION, APROBADA, RECHAZADA.");
        }
        request.setStatus(status);
        requestRepository.save(request);

        if (status == AdoptionRequestStatus.APROBADA) {
            AdoptionListing listing = listingRepository.findById(request.getListingId()).orElseThrow();
            listing.setStatus(AdoptionStatus.EN_PROCESO);
            listingRepository.save(listing);
        }

        notificationService.notify(
            request.getApplicantId(),
            NotificationType.ADOPCION,
            "Actualización de tu solicitud de adopción",
            "Tu solicitud ahora está: " + status
        );

        return request;
    }
}
