package com.cinemesh.service;

import com.cinemesh.common.PaymentStatus;
import com.cinemesh.model.Payment;
import com.cinemesh.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(String bookingReference, double amount, String paymentMethod, boolean simulateFailure) {
        String txId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if (simulateFailure) {
            log.warn("Simulated payment failure triggered for booking {}", bookingReference);
            Payment failedPayment = new Payment(txId, bookingReference, amount, paymentMethod, PaymentStatus.FAILED);
            return paymentRepository.save(failedPayment);
        }

        log.info("Payment processed successfully: txId={}, booking={}, amount=Rs.{}", txId, bookingReference, amount);
        Payment payment = new Payment(txId, bookingReference, amount, paymentMethod, PaymentStatus.SUCCESS);
        return paymentRepository.save(payment);
    }
}
