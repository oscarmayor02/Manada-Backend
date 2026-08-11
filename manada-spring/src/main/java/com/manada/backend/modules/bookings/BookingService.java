package com.manada.backend.modules.bookings;

import com.manada.backend.common.exception.ApiException;
import com.manada.backend.modules.bookings.dto.*;
import com.manada.backend.modules.notifications.NotificationService;
import com.manada.backend.modules.notifications.NotificationType;
import com.manada.backend.modules.providers.ProviderProfile;
import com.manada.backend.modules.providers.ProviderProfileRepository;
import com.manada.backend.modules.providers.ProviderVerificationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final ServiceOfferingRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final ProviderProfileRepository providerRepository;
    private final NotificationService notificationService;

    public BookingService(
            ServiceOfferingRepository serviceRepository,
            BookingRepository bookingRepository,
            ProviderProfileRepository providerRepository,
            NotificationService notificationService
    ) {
        this.serviceRepository = serviceRepository;
        this.bookingRepository = bookingRepository;
        this.providerRepository = providerRepository;
        this.notificationService = notificationService;
    }

    public List<ServiceOfferingResponse> listServices(ServiceType type) {
        List<ServiceOffering> services = type != null
            ? serviceRepository.findByActiveTrueAndServiceType(type)
            : serviceRepository.findByActiveTrue();
        return services.stream().map(ServiceOfferingResponse::from).toList();
    }

    @Transactional
    public ServiceOfferingResponse createService(UUID userId, ServiceOfferingRequest req) {
        ProviderProfile provider = providerRepository.findByUserId(userId)
            .orElseThrow(() -> ApiException.forbidden("Solo proveedores pueden publicar servicios."));
        if (provider.getVerificationStatus() != ProviderVerificationStatus.VERIFICADO) {
            throw ApiException.forbidden("Tu cuenta debe estar verificada para publicar servicios.");
        }

        ServiceOffering service = new ServiceOffering();
        service.setProviderId(provider.getId());
        service.setServiceType(req.serviceType());
        service.setName(req.name());
        service.setPrice(req.price());
        service.setDurationMin(req.durationMin());

        return ServiceOfferingResponse.from(serviceRepository.save(service));
    }

    @Transactional
    public BookingResponse createBooking(UUID buyerId, CreateBookingRequest req) {
        ServiceOffering service = serviceRepository.findById(req.serviceId())
            .orElseThrow(() -> ApiException.notFound("Servicio no encontrado."));
        ProviderProfile provider = providerRepository.findById(service.getProviderId())
            .orElseThrow(() -> ApiException.notFound("Proveedor no encontrado."));

        Booking booking = new Booking();
        booking.setServiceId(service.getId());
        booking.setBuyerId(buyerId);
        booking.setScheduledAt(req.scheduledAt());
        booking.setPrice(service.getPrice());
        booking.setCommissionAmt(service.getPrice().multiply(provider.getCommissionRate()));
        booking = bookingRepository.save(booking);

        notificationService.notify(
            buyerId, NotificationType.RESERVA, "Reserva creada",
            "Tu cita de " + service.getName() + " quedó pendiente de confirmación."
        );

        return BookingResponse.from(booking);
    }
}
