package com.manada.backend.modules.bookings;

import com.manada.backend.common.security.AuthenticatedUser;
import com.manada.backend.modules.bookings.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<ServiceOfferingResponse> listServices(@RequestParam(required = false) ServiceType type) {
        return bookingService.listServices(type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceOfferingResponse createService(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody ServiceOfferingRequest req) {
        return bookingService.createService(user.id(), req);
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@AuthenticationPrincipal AuthenticatedUser user, @Valid @RequestBody CreateBookingRequest req) {
        return bookingService.createBooking(user.id(), req);
    }
}
