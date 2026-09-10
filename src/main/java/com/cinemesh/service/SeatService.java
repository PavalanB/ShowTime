package com.cinemesh.service;

import com.cinemesh.common.SeatStatus;
import com.cinemesh.model.ShowSeat;
import com.cinemesh.repository.ShowSeatRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final ShowSeatRepository showSeatRepository;

    public SeatService(ShowSeatRepository showSeatRepository) {
        this.showSeatRepository = showSeatRepository;
    }

    public List<ShowSeat> getSeatLayout(Long showId) {
        LocalDateTime now = LocalDateTime.now();
        List<ShowSeat> seats = showSeatRepository.findByShowIdOrderByRowNameAscSeatColAsc(showId);
        // Normalize any expired held seats for the caller
        for (ShowSeat s : seats) {
            if (s.isExpired(now)) {
                s.setStatus(SeatStatus.AVAILABLE);
            }
        }
        return seats;
    }

    public boolean areSeatsAvailable(Long showId, Collection<String> seatNumbers) {
        LocalDateTime now = LocalDateTime.now();
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberIn(showId, seatNumbers);
        if (seats.size() != seatNumbers.size()) {
            return false;
        }
        return seats.stream().allMatch(s -> s.getStatus() == SeatStatus.AVAILABLE || s.isExpired(now));
    }
}
