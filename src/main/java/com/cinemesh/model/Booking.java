package com.cinemesh.model;

import com.cinemesh.common.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_ref", columnList = "bookingReference", unique = true),
    @Index(name = "idx_booking_tenant", columnList = "tenantId"),
    @Index(name = "idx_booking_customer", columnList = "customerId")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String bookingReference;

    @Column(nullable = false)
    private Long showId;

    @Column(nullable = false, length = 64)
    private String tenantId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 255)
    private String seatsCsv; // e.g. "A1,A2"

    @Column(nullable = false)
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(length = 64)
    private String lockToken;

    @Column(length = 64)
    private String paymentTransactionId;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Booking() {}

    public Booking(String bookingReference, Long showId, String tenantId, Long customerId, String seatsCsv, double totalAmount, String lockToken) {
        this.bookingReference = bookingReference;
        this.showId = showId;
        this.tenantId = tenantId;
        this.customerId = customerId;
        this.seatsCsv = seatsCsv;
        this.totalAmount = totalAmount;
        this.lockToken = lockToken;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getSeatsCsv() { return seatsCsv; }
    public void setSeatsCsv(String seatsCsv) { this.seatsCsv = seatsCsv; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public String getLockToken() { return lockToken; }
    public void setLockToken(String lockToken) { this.lockToken = lockToken; }

    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
