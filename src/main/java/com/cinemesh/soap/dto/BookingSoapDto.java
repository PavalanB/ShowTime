package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class BookingSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/booking";

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BookingDto {
        private Long bookingId;
        private String bookingReference;
        private Long showId;
        private String movieTitle;
        private String theatreName;
        private String screenName;
        private String showTime;
        private Long customerId;
        private String customerName;
        @XmlElement(name = "seats")
        private List<String> seats;
        private double totalAmount;
        private String status;
        private String createdAt;

        public Long getBookingId() { return bookingId; }
        public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
        public String getMovieTitle() { return movieTitle; }
        public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
        public String getTheatreName() { return theatreName; }
        public void setTheatreName(String theatreName) { this.theatreName = theatreName; }
        public String getScreenName() { return screenName; }
        public void setScreenName(String screenName) { this.screenName = screenName; }
        public String getShowTime() { return showTime; }
        public void setShowTime(String showTime) { this.showTime = showTime; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public List<String> getSeats() { return seats; }
        public void setSeats(List<String> seats) { this.seats = seats; }
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }

    @XmlRootElement(name = "CreateBookingRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreateBookingRequest {
        private Long showId;
        private Long customerId;
        private String lockToken;
        @XmlElement(name = "seatNumbers")
        private List<String> seatNumbers;

        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public String getLockToken() { return lockToken; }
        public void setLockToken(String lockToken) { this.lockToken = lockToken; }
        public List<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    }

    @XmlRootElement(name = "CreateBookingResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreateBookingResponse {
        private String status;
        private BookingDto booking;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BookingDto getBooking() { return booking; }
        public void setBooking(BookingDto booking) { this.booking = booking; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "ConfirmBookingRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfirmBookingRequest {
        private String bookingReference;
        private String paymentTransactionId;

        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public String getPaymentTransactionId() { return paymentTransactionId; }
        public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    }

    @XmlRootElement(name = "ConfirmBookingResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ConfirmBookingResponse {
        private String status;
        private BookingDto booking;
        private String ticketReference;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BookingDto getBooking() { return booking; }
        public void setBooking(BookingDto booking) { this.booking = booking; }
        public String getTicketReference() { return ticketReference; }
        public void setTicketReference(String ticketReference) { this.ticketReference = ticketReference; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "GetBookingRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetBookingRequest {
        private String bookingReference;
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    }

    @XmlRootElement(name = "GetBookingResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetBookingResponse {
        private String status;
        private BookingDto booking;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public BookingDto getBooking() { return booking; }
        public void setBooking(BookingDto booking) { this.booking = booking; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
