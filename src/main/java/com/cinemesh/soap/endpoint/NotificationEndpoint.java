package com.cinemesh.soap.endpoint;

import com.cinemesh.service.NotificationService;
import com.cinemesh.soap.dto.NotificationSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class NotificationEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/notification";
    private final NotificationService notificationService;

    public NotificationEndpoint(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SendBookingNotificationRequest")
    @ResponsePayload
    public SendBookingNotificationResponse sendNotification(@RequestPayload SendBookingNotificationRequest request) {
        SendBookingNotificationResponse response = new SendBookingNotificationResponse();
        String notificationId = notificationService.sendBookingConfirmation(
                request.getBookingReference(), request.getRecipientEmail(), request.getRecipientPhone(),
                request.getMovieTitle(), request.getShowTime(), request.getSeats()
        );
        response.setSuccess(true);
        response.setNotificationId(notificationId);
        response.setMessage("Notification dispatched to customer channel");
        return response;
    }
}
