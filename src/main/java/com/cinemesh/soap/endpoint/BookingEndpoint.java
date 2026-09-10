package com.cinemesh.soap.endpoint;

import com.cinemesh.model.*;
import com.cinemesh.service.BookingService;
import com.cinemesh.service.MovieService;
import com.cinemesh.service.ShowService;
import com.cinemesh.service.TenantService;
import com.cinemesh.service.TheatreService;
import com.cinemesh.soap.dto.BookingSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Arrays;
import java.util.Optional;

@Endpoint
public class BookingEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/booking";
    private final BookingService bookingService;
    private final ShowService showService;
    private final MovieService movieService;
    private final TenantService tenantService;
    private final TheatreService theatreService;

    public BookingEndpoint(BookingService bookingService, ShowService showService,
                           MovieService movieService, TenantService tenantService,
                           TheatreService theatreService) {
        this.bookingService = bookingService;
        this.showService = showService;
        this.movieService = movieService;
        this.tenantService = tenantService;
        this.theatreService = theatreService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateBookingRequest")
    @ResponsePayload
    public CreateBookingResponse createBooking(@RequestPayload CreateBookingRequest request) {
        CreateBookingResponse response = new CreateBookingResponse();
        try {
            Booking booking = bookingService.createBooking(
                    request.getShowId(), request.getCustomerId(), request.getLockToken(), request.getSeatNumbers()
            );
            response.setStatus("SUCCESS");
            response.setBooking(mapBooking(booking));
            response.setMessage("Booking created, awaiting payment confirmation");
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ConfirmBookingRequest")
    @ResponsePayload
    public ConfirmBookingResponse confirmBooking(@RequestPayload ConfirmBookingRequest request) {
        ConfirmBookingResponse response = new ConfirmBookingResponse();
        try {
            Booking booking = bookingService.confirmBooking(request.getBookingReference(), request.getPaymentTransactionId());
            response.setStatus("SUCCESS");
            response.setBooking(mapBooking(booking));
            response.setTicketReference("TCK-" + booking.getBookingReference().substring(4));
            response.setMessage("Booking successfully confirmed and ticket issued");
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetBookingRequest")
    @ResponsePayload
    public GetBookingResponse getBooking(@RequestPayload GetBookingRequest request) {
        GetBookingResponse response = new GetBookingResponse();
        Optional<Booking> bookingOpt = bookingService.getBookingByReference(request.getBookingReference());
        if (bookingOpt.isPresent()) {
            response.setStatus("SUCCESS");
            response.setBooking(mapBooking(bookingOpt.get()));
        } else {
            response.setStatus("NOT_FOUND");
            response.setMessage("Booking not found");
        }
        return response;
    }

    private BookingDto mapBooking(Booking b) {
        BookingDto dto = new BookingDto();
        dto.setBookingId(b.getId());
        dto.setBookingReference(b.getBookingReference());
        dto.setShowId(b.getShowId());
        dto.setCustomerId(b.getCustomerId());
        dto.setTotalAmount(b.getTotalAmount());
        dto.setStatus(b.getStatus().name());
        dto.setCreatedAt(b.getCreatedAt().toString());
        if (b.getSeatsCsv() != null) {
            dto.setSeats(Arrays.asList(b.getSeatsCsv().split(",")));
        }

        showService.getShowById(b.getShowId()).ifPresent(s -> {
            dto.setShowTime(s.getStartTime().toString());
            tenantService.getTenant(s.getTenantId()).ifPresent(t -> dto.setTheatreName(t.getName()));
            movieService.getMovieById(s.getMovieId()).ifPresent(m -> dto.setMovieTitle(m.getTitle()));
            theatreService.getScreenById(s.getScreenId()).ifPresent(sc -> dto.setScreenName(sc.getScreenName()));
        });

        return dto;
    }
}
