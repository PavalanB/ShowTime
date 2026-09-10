package com.cinemesh.service;

import com.cinemesh.common.BookingStatus;
import com.cinemesh.model.*;
import com.cinemesh.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final MovieRepository movieRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final CoordinationService coordinationService;
    private final TicketService ticketService;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository,
                          ShowRepository showRepository,
                          ShowSeatRepository showSeatRepository,
                          MovieRepository movieRepository,
                          TenantRepository tenantRepository,
                          UserRepository userRepository,
                          CoordinationService coordinationService,
                          TicketService ticketService,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.movieRepository = movieRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.coordinationService = coordinationService;
        this.ticketService = ticketService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Booking createBooking(Long showId, Long customerId, String lockToken, List<String> seatNumbers) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));

        boolean validLock = coordinationService.validateSeatLock(showId, lockToken, customerId, seatNumbers);
        if (!validLock) {
            throw new IllegalStateException("Seat reservation lock is invalid or has expired.");
        }

        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberIn(showId, seatNumbers);
        double totalAmount = seats.stream().mapToDouble(ShowSeat::getPrice).sum();

        String bookingRef = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String seatsCsv = String.join(",", seatNumbers);

        Booking booking = new Booking(bookingRef, showId, show.getTenantId(), customerId, seatsCsv, totalAmount, lockToken);
        Booking saved = bookingRepository.save(booking);
        log.info("Created pending booking {} for show {}, total Rs.{}", bookingRef, showId, totalAmount);
        return saved;
    }

    @Transactional
    public Booking confirmBooking(String bookingReference, String paymentTransactionId) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingReference));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            return booking;
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentTransactionId(paymentTransactionId);
        Booking savedBooking = bookingRepository.save(booking);

        // Transition seats from HELD to BOOKED
        coordinationService.confirmSeatBooking(booking.getShowId(), booking.getLockToken());

        // Generate Ticket
        ticketService.generateTicket(bookingReference);

        // Send Notification
        Show show = showRepository.findById(booking.getShowId()).orElse(null);
        String movieTitle = "Movie";
        String showTime = "Scheduled Time";
        if (show != null) {
            movieTitle = movieRepository.findById(show.getMovieId()).map(Movie::getTitle).orElse("Movie");
            showTime = show.getStartTime().toString();
        }

        String recipientEmail = "customer@showtime.com";
        Optional<User> customer = userRepository.findById(booking.getCustomerId());
        if (customer.isPresent() && customer.get().getEmail() != null) {
            recipientEmail = customer.get().getEmail();
        }

        notificationService.sendBookingConfirmation(
                bookingReference, recipientEmail, "", movieTitle, showTime,
                Arrays.asList(booking.getSeatsCsv().split(","))
        );

        log.info("Booking {} confirmed with payment {}", bookingReference, paymentTransactionId);
        return savedBooking;
    }

    @Transactional
    public Booking cancelBooking(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingReference));

        if (booking.getStatus() == BookingStatus.PENDING) {
            coordinationService.releaseSeatLock(booking.getShowId(), booking.getLockToken(), booking.getCustomerId());
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBookingByReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference);
    }

    public List<Booking> getBookingsByCustomer(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public List<Booking> getBookingsByTenant(String tenantId) {
        return bookingRepository.findByTenantId(tenantId);
    }
}
