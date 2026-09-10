package com.cinemesh.service;

import com.cinemesh.common.BookingStatus;
import com.cinemesh.common.SeatStatus;
import com.cinemesh.model.Booking;
import com.cinemesh.model.Screen;
import com.cinemesh.model.Show;
import com.cinemesh.model.ShowSeat;
import com.cinemesh.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final BookingRepository bookingRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final TenantRepository tenantRepository;

    public AnalyticsService(BookingRepository bookingRepository,
                            ScreenRepository screenRepository,
                            ShowRepository showRepository,
                            ShowSeatRepository showSeatRepository,
                            TenantRepository tenantRepository) {
        this.bookingRepository = bookingRepository;
        this.screenRepository = screenRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.tenantRepository = tenantRepository;
    }

    public static class TenantAnalyticsReport {
        private final String tenantId;
        private final String theatreName;
        private final long totalBookings;
        private final long totalTicketsSold;
        private final double totalRevenue;
        private final double averageOccupancyRate;
        private final int activeScreens;
        private final int scheduledShows;

        public TenantAnalyticsReport(String tenantId, String theatreName, long totalBookings, long totalTicketsSold,
                                     double totalRevenue, double averageOccupancyRate, int activeScreens, int scheduledShows) {
            this.tenantId = tenantId;
            this.theatreName = theatreName;
            this.totalBookings = totalBookings;
            this.totalTicketsSold = totalTicketsSold;
            this.totalRevenue = totalRevenue;
            this.averageOccupancyRate = averageOccupancyRate;
            this.activeScreens = activeScreens;
            this.scheduledShows = scheduledShows;
        }

        public String getTenantId() { return tenantId; }
        public String getTheatreName() { return theatreName; }
        public long getTotalBookings() { return totalBookings; }
        public long getTotalTicketsSold() { return totalTicketsSold; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getAverageOccupancyRate() { return averageOccupancyRate; }
        public int getActiveScreens() { return activeScreens; }
        public int getScheduledShows() { return scheduledShows; }
    }

    public TenantAnalyticsReport getTenantReport(String tenantId) {
        String theatreName = tenantRepository.findByTenantId(tenantId)
                .map(t -> t.getName() + " (" + t.getCity() + ")")
                .orElse(tenantId);

        List<Booking> bookings = bookingRepository.findByTenantId(tenantId);
        List<Screen> screens = screenRepository.findByTenantId(tenantId);
        List<Show> shows = showRepository.findByTenantId(tenantId);

        long confirmedBookings = 0;
        long ticketsSold = 0;
        double totalRevenue = 0.0;

        for (Booking b : bookings) {
            if (b.getStatus() == BookingStatus.CONFIRMED) {
                confirmedBookings++;
                totalRevenue += b.getTotalAmount();
                if (b.getSeatsCsv() != null && !b.getSeatsCsv().isEmpty()) {
                    ticketsSold += b.getSeatsCsv().split(",").length;
                }
            }
        }

        long totalSeatsInShows = 0;
        long bookedSeatsInShows = 0;

        for (Show show : shows) {
            List<ShowSeat> seats = showSeatRepository.findByShowIdOrderByRowNameAscSeatColAsc(show.getId());
            totalSeatsInShows += seats.size();
            bookedSeatsInShows += seats.stream().filter(s -> s.getStatus() == SeatStatus.BOOKED).count();
        }

        double occupancyRate = totalSeatsInShows > 0 ? ((double) bookedSeatsInShows / totalSeatsInShows) * 100.0 : 0.0;

        return new TenantAnalyticsReport(
                tenantId, theatreName, confirmedBookings, ticketsSold, totalRevenue,
                Math.round(occupancyRate * 10.0) / 10.0, screens.size(), shows.size()
        );
    }
}
