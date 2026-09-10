package com.cinemesh.soap.endpoint;

import com.cinemesh.model.Payment;
import com.cinemesh.service.PaymentService;
import com.cinemesh.soap.dto.PaymentSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PaymentEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/payment";
    private final PaymentService paymentService;

    public PaymentEndpoint(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessPaymentRequest")
    @ResponsePayload
    public ProcessPaymentResponse processPayment(@RequestPayload ProcessPaymentRequest request) {
        ProcessPaymentResponse response = new ProcessPaymentResponse();
        Payment payment = paymentService.processPayment(
                request.getBookingReference(), request.getAmount(), request.getPaymentMethod(), request.isSimulateFailure()
        );

        boolean success = payment.getStatus() == com.cinemesh.common.PaymentStatus.SUCCESS;
        response.setSuccess(success);
        response.setTransactionId(payment.getTransactionId());

        PaymentDto dto = new PaymentDto();
        dto.setPaymentId(payment.getId());
        dto.setTransactionId(payment.getTransactionId());
        dto.setBookingReference(payment.getBookingReference());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus().name());
        dto.setProcessedAt(payment.getProcessedAt().toString());
        response.setPayment(dto);

        response.setMessage(success ? "Payment authorized successfully" : "Payment authorization declined by simulated gateway");
        return response;
    }
}
