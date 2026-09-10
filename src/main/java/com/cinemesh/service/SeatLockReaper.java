package com.cinemesh.service;

import com.cinemesh.common.SeatStatus;
import com.cinemesh.repository.ShowSeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class SeatLockReaper {

    private static final Logger log = LoggerFactory.getLogger(SeatLockReaper.class);

    private final ShowSeatRepository showSeatRepository;

    public SeatLockReaper(ShowSeatRepository showSeatRepository) {
        this.showSeatRepository = showSeatRepository;
    }

    /**
     * Periodically sweeps the database to release expired seat reservations.
     * Guarantees that seats never stay locked if a user abandons their checkout cart.
     */
    @Scheduled(fixedDelayString = "${cinemesh.locking.reaper-interval-ms:5000}")
    @Transactional
    public void reapExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        int reaped = showSeatRepository.reapExpiredSeatLocks(SeatStatus.HELD, SeatStatus.AVAILABLE, now);
        if (reaped > 0) {
            log.info("SeatLockReaper: Reaped and freed {} expired seat reservations back to AVAILABLE.", reaped);
        }
    }
}
