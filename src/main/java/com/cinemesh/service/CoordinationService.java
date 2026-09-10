package com.cinemesh.service;

import com.cinemesh.common.SeatStatus;
import com.cinemesh.model.ShowSeat;
import com.cinemesh.repository.ShowSeatRepository;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoordinationService {

    private static final Logger log = LoggerFactory.getLogger(CoordinationService.class);

    private final ShowSeatRepository showSeatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CoordinationService(ShowSeatRepository showSeatRepository, @Lazy SimpMessagingTemplate messagingTemplate) {
        this.showSeatRepository = showSeatRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public static class LockResult {
        private final boolean success;
        private final String lockToken;
        @JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        private final LocalDateTime expiresAt;
        private final List<String> lockedSeats;
        private final List<String> failedSeats;
        private final String message;

        public LockResult(boolean success, String lockToken, LocalDateTime expiresAt, List<String> lockedSeats, List<String> failedSeats, String message) {
            this.success = success;
            this.lockToken = lockToken;
            this.expiresAt = expiresAt;
            this.lockedSeats = lockedSeats;
            this.failedSeats = failedSeats;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public String getLockToken() { return lockToken; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public List<String> getLockedSeats() { return lockedSeats; }
        public List<String> getFailedSeats() { return failedSeats; }
        public String getMessage() { return message; }
    }

    /**
     * Atomically acquires a temporary distributed lock on the specified seats.
     * Prevents race conditions and double-booking using conditional atomic DB updates.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public synchronized LockResult acquireSeatLock(Long showId, Long customerId, List<String> seatNumbers, int lockDurationSeconds) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(lockDurationSeconds > 0 ? lockDurationSeconds : 300);
        String lockToken = UUID.randomUUID().toString();

        // Check current seat states
        List<ShowSeat> existingSeats = showSeatRepository.findByShowIdAndSeatNumberIn(showId, seatNumbers);
        List<String> unavailable = new ArrayList<>();

        for (ShowSeat s : existingSeats) {
            if (s.getStatus() == SeatStatus.BOOKED) {
                unavailable.add(s.getSeatNumber() + " (Already Booked)");
            } else if (s.getStatus() == SeatStatus.HELD && !s.isExpired(now)) {
                unavailable.add(s.getSeatNumber() + " (Held by another customer)");
            }
        }

        if (!unavailable.isEmpty()) {
            return new LockResult(false, null, null, Collections.emptyList(), unavailable,
                    "Seats unavailable: " + String.join(", ", unavailable));
        }

        int updatedCount = showSeatRepository.lockSeatsAtomically(
                showId, seatNumbers, SeatStatus.HELD, SeatStatus.AVAILABLE, customerId, lockToken, expiresAt, now
        );

        if (updatedCount == seatNumbers.size()) {
            log.info("Successfully locked seats {} for show {} by customer {} with token {}", seatNumbers, showId, customerId, lockToken);
            broadcastSeatUpdate(showId, seatNumbers, "HELD");
            return new LockResult(true, lockToken, expiresAt, seatNumbers, Collections.emptyList(), "Seats successfully held.");
        } else {
            // Atomic rollback of any partially updated seats
            showSeatRepository.releaseSeatsByLockToken(showId, lockToken, SeatStatus.AVAILABLE);
            log.warn("Lock conflict detected for show {} seats {}. Expected {}, updated {}", showId, seatNumbers, seatNumbers.size(), updatedCount);
            return new LockResult(false, null, null, Collections.emptyList(), seatNumbers,
                    "Concurrent booking collision: one or more seats were locked by another customer.");
        }
    }

    @Transactional
    public boolean releaseSeatLock(Long showId, String lockToken, Long customerId) {
        List<ShowSeat> lockedSeats = showSeatRepository.findByShowIdAndLockToken(showId, lockToken);
        if (lockedSeats.isEmpty()) {
            return false;
        }
        showSeatRepository.releaseSeatsByLockToken(showId, lockToken, SeatStatus.AVAILABLE);
        List<String> seatNums = lockedSeats.stream().map(ShowSeat::getSeatNumber).collect(Collectors.toList());
        log.info("Released seats {} for show {} with token {}", seatNums, showId, lockToken);
        broadcastSeatUpdate(showId, seatNums, "AVAILABLE");
        return true;
    }

    public boolean validateSeatLock(Long showId, String lockToken, Long customerId, Collection<String> seatNumbers) {
        LocalDateTime now = LocalDateTime.now();
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndLockToken(showId, lockToken);
        if (seats.size() != seatNumbers.size()) {
            return false;
        }
        for (ShowSeat s : seats) {
            if (!seatNumbers.contains(s.getSeatNumber()) || s.getStatus() != SeatStatus.HELD || s.isExpired(now)) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public void confirmSeatBooking(Long showId, String lockToken) {
        List<ShowSeat> lockedSeats = showSeatRepository.findByShowIdAndLockToken(showId, lockToken);
        showSeatRepository.confirmSeatsBooking(showId, lockToken, SeatStatus.HELD, SeatStatus.BOOKED);
        List<String> seatNums = lockedSeats.stream().map(ShowSeat::getSeatNumber).collect(Collectors.toList());
        log.info("Confirmed booking for seats {} in show {}", seatNums, showId);
        broadcastSeatUpdate(showId, seatNums, "BOOKED");
    }

    private void broadcastSeatUpdate(Long showId, List<String> seatNumbers, String status) {
        try {
            if (messagingTemplate != null) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("showId", showId);
                payload.put("seats", seatNumbers);
                payload.put("status", status);
                payload.put("timestamp", System.currentTimeMillis());
                messagingTemplate.convertAndSend("/topic/seats/" + showId, payload);
            }
        } catch (Exception e) {
            log.debug("WebSocket broadcast skipped: {}", e.getMessage());
        }
    }
}
