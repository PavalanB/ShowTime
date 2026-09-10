package com.cinemesh.repository;

import com.cinemesh.common.BookingStatus;
import com.cinemesh.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByTenantId(String tenantId);
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByShowId(Long showId);
    long countByTenantIdAndStatus(String tenantId, BookingStatus status);
}
