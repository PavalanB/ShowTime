package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;

public class PaymentSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/payment";

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PaymentDto {
        private Long paymentId;
        private String transactionId;
        private String bookingReference;
        private double amount;
        private String paymentMethod;
        private String status;
        private String processedAt;

        public Long getPaymentId() { return paymentId; }
        public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getProcessedAt() { return processedAt; }
        public void setProcessedAt(String processedAt) { this.processedAt = processedAt; }
    }

    @XmlRootElement(name = "ProcessPaymentRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ProcessPaymentRequest {
        private String bookingReference;
        private double amount;
        private String paymentMethod;
        private boolean simulateFailure;

        public String getBookingReference() { return bookingReference; }
        public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public boolean isSimulateFailure() { return simulateFailure; }
        public void setSimulateFailure(boolean simulateFailure) { this.simulateFailure = simulateFailure; }
    }

    @XmlRootElement(name = "ProcessPaymentResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ProcessPaymentResponse {
        private boolean success;
        private PaymentDto payment;
        private String transactionId;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public PaymentDto getPayment() { return payment; }
        public void setPayment(PaymentDto payment) { this.payment = payment; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
