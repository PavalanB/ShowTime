package com.cinemesh.model;

import com.cinemesh.common.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_tx", columnList = "transactionId", unique = true),
    @Index(name = "idx_payment_booking", columnList = "bookingReference")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String transactionId;

    @Column(nullable = false, length = 64)
    private String bookingReference;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false, length = 32)
    private String paymentMethod; // CARD, UPI, NETBANKING

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PaymentStatus status;

    private LocalDateTime processedAt = LocalDateTime.now();

    public Payment() {}

    public Payment(String transactionId, String bookingReference, double amount, String paymentMethod, PaymentStatus status) {
        this.transactionId = transactionId;
        this.bookingReference = bookingReference;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
