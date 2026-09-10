package com.cinemesh;

import com.cinemesh.common.SeatStatus;
import com.cinemesh.model.Booking;
import com.cinemesh.model.ShowSeat;
import com.cinemesh.repository.ShowSeatRepository;
import com.cinemesh.service.BookingService;
import com.cinemesh.service.CoordinationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConcurrencyBookingStressTest {

    private static final Logger log = LoggerFactory.getLogger(ConcurrencyBookingStressTest.class);

    @Autowired
    private CoordinationService coordinationService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Test
    @DisplayName("50 Concurrent Threads Competing for Identical Seats -> Exactly 1 Winner, 0 Double Bookings")
    public void testConcurrentSeatReservation() throws InterruptedException {
        int numberOfThreads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Long showId = 1L;
        List<String> targetSeats = Arrays.asList("A1", "A2");

        for (int i = 0; i < numberOfThreads; i++) {
            final long customerId = 1000L + i;
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await(); // All 50 threads release at the exact same millisecond!
                    CoordinationService.LockResult lock = coordinationService.acquireSeatLock(
                            showId, customerId, targetSeats, 300
                    );

                    if (lock.isSuccess()) {
                        successCount.incrementAndGet();
                        Booking booking = bookingService.createBooking(showId, customerId, lock.getLockToken(), targetSeats);
                        bookingService.confirmBooking(booking.getBookingReference(), "TXN-TEST-" + customerId);
                    } else {
                        failureCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown(); // FIRE!
        assertTrue(doneLatch.await(15, TimeUnit.SECONDS), "Concurrent test execution timed out");

        log.info("Concurrency Test Result: Successes = {}, Failures = {}", successCount.get(), failureCount.get());

        // Assert that strictly ONE customer won the seats and 49 failed cleanly
        assertEquals(1, successCount.get(), "Exactly 1 thread must successfully lock and book the seats.");
        assertEquals(numberOfThreads - 1, failureCount.get(), "All other competing threads must be safely rejected.");

        // Assert database state consistency
        List<ShowSeat> seats = showSeatRepository.findByShowIdAndSeatNumberIn(showId, targetSeats);
        for (ShowSeat seat : seats) {
            assertEquals(SeatStatus.BOOKED, seat.getStatus(), "Seat " + seat.getSeatNumber() + " must be BOOKED.");
        }

        executorService.shutdown();
    }
}
