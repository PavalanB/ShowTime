package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class CoordinationSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/coordination";

    @XmlRootElement(name = "AcquireSeatLockRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AcquireSeatLockRequest {
        private Long showId;
        private Long customerId;
        @XmlElement(name = "seatNumbers")
        private List<String> seatNumbers;
        private int lockDurationSeconds = 300;

        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public List<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
        public int getLockDurationSeconds() { return lockDurationSeconds; }
        public void setLockDurationSeconds(int lockDurationSeconds) { this.lockDurationSeconds = lockDurationSeconds; }
    }

    @XmlRootElement(name = "AcquireSeatLockResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AcquireSeatLockResponse {
        private boolean success;
        private String lockToken;
        private String expiresAt;
        @XmlElement(name = "lockedSeats")
        private List<String> lockedSeats;
        @XmlElement(name = "failedSeats")
        private List<String> failedSeats;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getLockToken() { return lockToken; }
        public void setLockToken(String lockToken) { this.lockToken = lockToken; }
        public String getExpiresAt() { return expiresAt; }
        public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
        public List<String> getLockedSeats() { return lockedSeats; }
        public void setLockedSeats(List<String> lockedSeats) { this.lockedSeats = lockedSeats; }
        public List<String> getFailedSeats() { return failedSeats; }
        public void setFailedSeats(List<String> failedSeats) { this.failedSeats = failedSeats; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "ReleaseSeatLockRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ReleaseSeatLockRequest {
        private Long showId;
        private String lockToken;
        private Long customerId;

        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
        public String getLockToken() { return lockToken; }
        public void setLockToken(String lockToken) { this.lockToken = lockToken; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
    }

    @XmlRootElement(name = "ReleaseSeatLockResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ReleaseSeatLockResponse {
        private boolean success;
        @XmlElement(name = "releasedSeats")
        private List<String> releasedSeats;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public List<String> getReleasedSeats() { return releasedSeats; }
        public void setReleasedSeats(List<String> releasedSeats) { this.releasedSeats = releasedSeats; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "ValidateSeatLockRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ValidateSeatLockRequest {
        private Long showId;
        private String lockToken;
        private Long customerId;
        @XmlElement(name = "seatNumbers")
        private List<String> seatNumbers;

        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
        public String getLockToken() { return lockToken; }
        public void setLockToken(String lockToken) { this.lockToken = lockToken; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public List<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    }

    @XmlRootElement(name = "ValidateSeatLockResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ValidateSeatLockResponse {
        private boolean valid;
        private String message;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
