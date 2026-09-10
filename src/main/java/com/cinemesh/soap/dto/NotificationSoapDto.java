package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class NotificationSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/notification";

    @XmlRootElement(name = "SendBookingNotificationRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SendBookingNotificationRequest {
        private String bookingReference;
        private String recipientEmail;
        private String recipientPhone;
        private String movieTitle;
        private String showTime;
        @XmlElement(name = "seats")
        private List<String> seats;

        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public String getRecipientEmail() { return recipientEmail; }
        public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
        public String getRecipientPhone() { return recipientPhone; }
        public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
        public String getMovieTitle() { return movieTitle; }
        public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
        public String getShowTime() { return showTime; }
        public void setShowTime(String showTime) { this.showTime = showTime; }
        public List<String> getSeats() { return seats; }
        public void setSeats(List<String> seats) { this.seats = seats; }
    }

    @XmlRootElement(name = "SendBookingNotificationResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SendBookingNotificationResponse {
        private boolean success;
        private String notificationId;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getNotificationId() { return notificationId; }
        public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
