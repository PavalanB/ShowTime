package com.cinemesh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public String sendBookingConfirmation(String bookingReference, String recipientEmail, String recipientPhone,
                                          String movieTitle, String showTime, List<String> seats) {
        String notificationId = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[NOTIFICATION DISPATCHED] ID: {}, Recipient: {}, Movie: {}, Show: {}, Seats: {}, BookingRef: {}",
                notificationId, recipientEmail, movieTitle, showTime, String.join(", ", seats), bookingReference);
        return notificationId;
    }
}
