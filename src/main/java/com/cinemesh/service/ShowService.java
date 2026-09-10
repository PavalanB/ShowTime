package com.cinemesh.service;

import com.cinemesh.model.Screen;
import com.cinemesh.model.Show;
import com.cinemesh.model.ShowSeat;
import com.cinemesh.repository.ScreenRepository;
import com.cinemesh.repository.ShowRepository;
import com.cinemesh.repository.ShowSeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final ScreenRepository screenRepository;
    private final ShowSeatRepository showSeatRepository;

    public ShowService(ShowRepository showRepository, ScreenRepository screenRepository, ShowSeatRepository showSeatRepository) {
        this.showRepository = showRepository;
        this.screenRepository = screenRepository;
        this.showSeatRepository = showSeatRepository;
    }

    @Transactional
    public Show scheduleShow(String tenantId, Long movieId, Long screenNumber, LocalDateTime startTime, double standardPrice, double premiumPrice, double vipPrice) {
        Screen screen = screenRepository.findByTenantIdAndScreenNumber(tenantId, screenNumber.intValue())
                .orElseGet(() -> {
                    String screenName = (screenNumber == 2) ? "Premium Screen" : "Standard Screen";
                    Screen newScreen = new Screen(tenantId, screenNumber.intValue(), screenName, 6, 8);
                    return screenRepository.save(newScreen);
                });

        Show show = new Show(tenantId, movieId, screen.getId(), startTime, standardPrice, premiumPrice, vipPrice);
        Show savedShow = showRepository.save(show);

        // Generate the physical seat layout for this show
        List<ShowSeat> seats = new ArrayList<>();
        int totalRows = screen.getTotalRows();
        int seatsPerRow = screen.getSeatsPerRow();

        for (int r = 0; r < totalRows; r++) {
            char rowChar = (char) ('A' + r);
            String rowName = String.valueOf(rowChar);

            String category;
            double price;
            if (r < 2) {
                category = "VIP";
                price = vipPrice;
            } else if (r < 5) {
                category = "PREMIUM";
                price = premiumPrice;
            } else {
                category = "STANDARD";
                price = standardPrice;
            }

            for (int col = 1; col <= seatsPerRow; col++) {
                String seatNumber = rowName + col;
                ShowSeat seat = new ShowSeat(savedShow.getId(), seatNumber, rowName, col, category, price);
                seats.add(seat);
            }
        }
        showSeatRepository.saveAll(seats);

        return savedShow;
    }

    @Transactional
    public Show updateShow(Long showId, LocalDateTime startTime, double standardPrice, double premiumPrice, double vipPrice) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));
        
        show.setStartTime(startTime);
        show.setStandardPrice(standardPrice);
        show.setPremiumPrice(premiumPrice);
        show.setVipPrice(vipPrice);
        
        // Update unbooked seat prices
        List<ShowSeat> seats = showSeatRepository.findByShowIdOrderByRowNameAscSeatColAsc(showId);
        for(ShowSeat seat : seats) {
            if(seat.getStatus() == com.cinemesh.common.SeatStatus.AVAILABLE) {
                if("VIP".equals(seat.getCategory())) seat.setPrice(vipPrice);
                else if("PREMIUM".equals(seat.getCategory())) seat.setPrice(premiumPrice);
                else if("STANDARD".equals(seat.getCategory())) seat.setPrice(standardPrice);
            }
        }
        showSeatRepository.saveAll(seats);
        return showRepository.save(show);
    }

    public List<Show> getShowsByTenant(String tenantId) {
        return showRepository.findByTenantId(tenantId);
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public Optional<Show> getShowById(Long showId) {
        return showRepository.findById(showId);
    }

    @Transactional
    public void deleteShow(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));
        List<ShowSeat> seats = showSeatRepository.findByShowIdOrderByRowNameAscSeatColAsc(showId);
        boolean hasBookedSeats = seats.stream().anyMatch(s -> s.getStatus() == com.cinemesh.common.SeatStatus.BOOKED);
        if (hasBookedSeats) {
            throw new IllegalStateException("Cannot delete show with active bookings.");
        }
        showSeatRepository.deleteAll(seats);
        showRepository.delete(show);
    }
}
