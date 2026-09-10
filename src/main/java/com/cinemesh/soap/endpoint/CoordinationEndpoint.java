package com.cinemesh.soap.endpoint;

import com.cinemesh.service.CoordinationService;
import com.cinemesh.service.CoordinationService.LockResult;
import com.cinemesh.soap.dto.CoordinationSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CoordinationEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/coordination";
    private final CoordinationService coordinationService;

    public CoordinationEndpoint(CoordinationService coordinationService) {
        this.coordinationService = coordinationService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AcquireSeatLockRequest")
    @ResponsePayload
    public AcquireSeatLockResponse acquireSeatLock(@RequestPayload AcquireSeatLockRequest request) {
        AcquireSeatLockResponse response = new AcquireSeatLockResponse();
        LockResult result = coordinationService.acquireSeatLock(
                request.getShowId(), request.getCustomerId(), request.getSeatNumbers(), request.getLockDurationSeconds()
        );

        response.setSuccess(result.isSuccess());
        response.setLockToken(result.getLockToken());
        response.setExpiresAt(result.getExpiresAt() != null ? result.getExpiresAt().toString() : null);
        response.setLockedSeats(result.getLockedSeats());
        response.setFailedSeats(result.getFailedSeats());
        response.setMessage(result.getMessage());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ReleaseSeatLockRequest")
    @ResponsePayload
    public ReleaseSeatLockResponse releaseSeatLock(@RequestPayload ReleaseSeatLockRequest request) {
        ReleaseSeatLockResponse response = new ReleaseSeatLockResponse();
        boolean released = coordinationService.releaseSeatLock(request.getShowId(), request.getLockToken(), request.getCustomerId());
        response.setSuccess(released);
        response.setMessage(released ? "Seats successfully unlocked" : "No active locks found for token");
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidateSeatLockRequest")
    @ResponsePayload
    public ValidateSeatLockResponse validateSeatLock(@RequestPayload ValidateSeatLockRequest request) {
        ValidateSeatLockResponse response = new ValidateSeatLockResponse();
        boolean valid = coordinationService.validateSeatLock(
                request.getShowId(), request.getLockToken(), request.getCustomerId(), request.getSeatNumbers()
        );
        response.setValid(valid);
        response.setMessage(valid ? "Lock is valid and active" : "Lock has expired or is invalid");
        return response;
    }
}
