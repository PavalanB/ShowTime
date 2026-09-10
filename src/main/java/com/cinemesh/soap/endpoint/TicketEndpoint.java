package com.cinemesh.soap.endpoint;

import com.cinemesh.model.Booking;
import com.cinemesh.model.Ticket;
import com.cinemesh.service.BookingService;
import com.cinemesh.service.TicketService;
import com.cinemesh.soap.dto.TicketSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Arrays;
import java.util.Optional;

@Endpoint
public class TicketEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/ticket";
    private final TicketService ticketService;
    private final BookingService bookingService;

    public TicketEndpoint(TicketService ticketService, BookingService bookingService) {
        this.ticketService = ticketService;
        this.bookingService = bookingService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GenerateTicketRequest")
    @ResponsePayload
    public GenerateTicketResponse generateTicket(@RequestPayload GenerateTicketRequest request) {
        GenerateTicketResponse response = new GenerateTicketResponse();
        try {
            Ticket ticket = ticketService.generateTicket(request.getBookingReference());
            response.setStatus("SUCCESS");
            response.setTicket(mapTicket(ticket));
            response.setMessage("Digital ticket generated");
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidateTicketRequest")
    @ResponsePayload
    public ValidateTicketResponse validateTicket(@RequestPayload ValidateTicketRequest request) {
        ValidateTicketResponse response = new ValidateTicketResponse();
        boolean valid = ticketService.validateTicket(request.getTicketReference());
        response.setValid(valid);
        response.setStatus(valid ? "VALIDATED" : "INVALID_OR_ALREADY_USED");
        response.setMessage(valid ? "Entry granted: ticket validated." : "Entry denied: invalid or used ticket.");

        Optional<Ticket> ticketOpt = ticketService.getTicketByReference(request.getTicketReference());
        ticketOpt.ifPresent(ticket -> response.setTicket(mapTicket(ticket)));
        return response;
    }

    private TicketDto mapTicket(Ticket t) {
        TicketDto dto = new TicketDto();
        dto.setTicketId(t.getId());
        dto.setTicketReference(t.getTicketReference());
        dto.setBookingReference(t.getBookingReference());
        dto.setQrCodePayload(t.getQrCodePayload());
        dto.setValidationStatus(t.getValidationStatus());
        dto.setIssuedAt(t.getIssuedAt().toString());

        bookingService.getBookingByReference(t.getBookingReference()).ifPresent(b -> {
            dto.setTotalAmount(b.getTotalAmount());
            if (b.getSeatsCsv() != null) {
                dto.setSeatNumbers(Arrays.asList(b.getSeatsCsv().split(",")));
            }
        });
        return dto;
    }
}
