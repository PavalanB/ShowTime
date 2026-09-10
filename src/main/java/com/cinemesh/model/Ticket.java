package com.cinemesh.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", indexes = {
    @Index(name = "idx_ticket_ref", columnList = "ticketReference", unique = true),
    @Index(name = "idx_ticket_booking", columnList = "bookingReference")
})
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String ticketReference;

    @Column(nullable = false, length = 64)
    private String bookingReference;

    @Lob
    @Column(length = 2000)
    private String qrCodePayload;

    @Column(nullable = false, length = 32)
    private String validationStatus = "ISSUED"; // ISSUED, VALIDATED, CANCELLED

    private LocalDateTime issuedAt = LocalDateTime.now();
    private LocalDateTime validatedAt;

    public Ticket() {}

    public Ticket(String ticketReference, String bookingReference, String qrCodePayload) {
        this.ticketReference = ticketReference;
        this.bookingReference = bookingReference;
        this.qrCodePayload = qrCodePayload;
        this.validationStatus = "ISSUED";
        this.issuedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketReference() { return ticketReference; }
    public void setTicketReference(String ticketReference) { this.ticketReference = ticketReference; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getQrCodePayload() { return qrCodePayload; }
    public void setQrCodePayload(String qrCodePayload) { this.qrCodePayload = qrCodePayload; }

    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
}
