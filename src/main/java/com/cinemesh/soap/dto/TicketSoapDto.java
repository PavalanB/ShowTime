package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class TicketSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/ticket";

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class TicketDto {
        private Long ticketId;
        private String ticketReference;
        private String bookingReference;
        private String movieTitle;
        private String theatreName;
        private String screenName;
        private String showTime;
        private String customerName;
        @XmlElement(name = "seatNumbers")
        private List<String> seatNumbers;
        private double totalAmount;
        private String qrCodePayload;
        private String validationStatus;
        private String issuedAt;

        public Long getTicketId() { return ticketId; }
        public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
        public String getTicketReference() { return ticketReference; }
        public void setTicketReference(String ticketReference) { this.ticketReference = ticketReference; }
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public String getMovieTitle() { return movieTitle; }
        public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
        public String getTheatreName() { return theatreName; }
        public void setTheatreName(String theatreName) { this.theatreName = theatreName; }
        public String getScreenName() { return screenName; }
        public void setScreenName(String screenName) { this.screenName = screenName; }
        public String getShowTime() { return showTime; }
        public void setShowTime(String showTime) { this.showTime = showTime; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public List<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        public String getQrCodePayload() { return qrCodePayload; }
        public void setQrCodePayload(String qrCodePayload) { this.qrCodePayload = qrCodePayload; }
        public String getValidationStatus() { return validationStatus; }
        public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }
        public String getIssuedAt() { return issuedAt; }
        public void setIssuedAt(String issuedAt) { this.issuedAt = issuedAt; }
    }

    @XmlRootElement(name = "GenerateTicketRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GenerateTicketRequest {
        private String bookingReference;
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    }

    @XmlRootElement(name = "GenerateTicketResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GenerateTicketResponse {
        private String status;
        private TicketDto ticket;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public TicketDto getTicket() { return ticket; }
        public void setTicket(TicketDto ticket) { this.ticket = ticket; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "ValidateTicketRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ValidateTicketRequest {
        private String ticketReference;
        private String tenantId;

        public String getTicketReference() { return ticketReference; }
        public void setTicketReference(String ticketReference) { this.ticketReference = ticketReference; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    }

    @XmlRootElement(name = "ValidateTicketResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ValidateTicketResponse {
        private boolean valid;
        private String status;
        private TicketDto ticket;
        private String message;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public TicketDto getTicket() { return ticket; }
        public void setTicket(TicketDto ticket) { this.ticket = ticket; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
