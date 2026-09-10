package com.cinemesh.repository;

import com.cinemesh.common.SeatStatus;
import com.cinemesh.model.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShowIdOrderByRowNameAscSeatColAsc(Long showId);

    Optional<ShowSeat> findByShowIdAndSeatNumber(Long showId, String seatNumber);

    List<ShowSeat> findByShowIdAndSeatNumberIn(Long showId, Collection<String> seatNumbers);

    List<ShowSeat> findByStatusAndLockExpiresAtBefore(SeatStatus status, LocalDateTime dateTime);

    List<ShowSeat> findByShowIdAndLockToken(Long showId, String lockToken);

    /**
     * Atomic distributed seat locking query:
     * Only transitions seats if they are currently AVAILABLE or have expired their hold TTL.
     * Returns the exact number of rows successfully updated.
     */
    @Modifying
    @Query("UPDATE ShowSeat s SET s.status = :heldStatus, s.lockedByUserId = :userId, s.lockToken = :lockToken, s.lockExpiresAt = :expiresAt " +
           "WHERE s.showId = :showId AND s.seatNumber IN :seatNumbers " +
           "AND (s.status = :availableStatus OR (s.status = :heldStatus AND s.lockExpiresAt < :now))")
    int lockSeatsAtomically(
            @Param("showId") Long showId,
            @Param("seatNumbers") Collection<String> seatNumbers,
            @Param("heldStatus") SeatStatus heldStatus,
            @Param("availableStatus") SeatStatus availableStatus,
            @Param("userId") Long userId,
            @Param("lockToken") String lockToken,
            @Param("expiresAt") LocalDateTime expiresAt,
            @Param("now") LocalDateTime now
    );

    /**
     * Atomically releases seats matching the given lockToken back to AVAILABLE.
     */
    @Modifying
    @Query("UPDATE ShowSeat s SET s.status = :availableStatus, s.lockedByUserId = null, s.lockToken = null, s.lockExpiresAt = null " +
           "WHERE s.showId = :showId AND s.lockToken = :lockToken")
    int releaseSeatsByLockToken(
            @Param("showId") Long showId,
            @Param("lockToken") String lockToken,
            @Param("availableStatus") SeatStatus availableStatus
    );

    /**
     * Atomically confirms seats from HELD to BOOKED.
     */
    @Modifying
    @Query("UPDATE ShowSeat s SET s.status = :bookedStatus, s.lockToken = null, s.lockExpiresAt = null " +
           "WHERE s.showId = :showId AND s.lockToken = :lockToken AND s.status = :heldStatus")
    int confirmSeatsBooking(
            @Param("showId") Long showId,
            @Param("lockToken") String lockToken,
            @Param("heldStatus") SeatStatus heldStatus,
            @Param("bookedStatus") SeatStatus bookedStatus
    );

    /**
     * Atomically expires timed-out HELD seats back to AVAILABLE.
     */
    @Modifying
    @Query("UPDATE ShowSeat s SET s.status = :availableStatus, s.lockedByUserId = null, s.lockToken = null, s.lockExpiresAt = null " +
           "WHERE s.status = :heldStatus AND s.lockExpiresAt < :now")
    int reapExpiredSeatLocks(
            @Param("heldStatus") SeatStatus heldStatus,
            @Param("availableStatus") SeatStatus availableStatus,
            @Param("now") LocalDateTime now
    );
}
