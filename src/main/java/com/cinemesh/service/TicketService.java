package com.cinemesh.service;

import com.cinemesh.model.Ticket;
import com.cinemesh.repository.TicketRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket generateTicket(String bookingReference) {
        String ticketRef = "TCK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String qrPayload = generateQrCodeBase64("SHOWTIME:" + ticketRef + ":" + bookingReference);

        Ticket ticket = new Ticket(ticketRef, bookingReference, qrPayload);
        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> getTicketByReference(String ticketReference) {
        return ticketRepository.findByTicketReference(ticketReference);
    }

    public Optional<Ticket> getTicketByBookingReference(String bookingReference) {
        return ticketRepository.findByBookingReference(bookingReference);
    }

    public boolean validateTicket(String ticketReference) {
        Optional<Ticket> ticketOpt = ticketRepository.findByTicketReference(ticketReference);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            if ("ISSUED".equals(ticket.getValidationStatus())) {
                ticket.setValidationStatus("VALIDATED");
                ticket.setValidatedAt(LocalDateTime.now());
                ticketRepository.save(ticket);
                log.info("Ticket {} validated successfully.", ticketReference);
                return true;
            }
        }
        return false;
    }

    private String generateQrCodeBase64(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Failed to generate QR code: {}", e.getMessage());
            return "data:text/plain;charset=utf-8," + text;
        }
    }
}
