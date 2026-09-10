package com.cinemesh.model;

import com.cinemesh.common.SeatStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "show_seats", indexes = {
    @Index(name = "idx_seat_show", columnList = "showId"),
    @Index(name = "idx_seat_show_num", columnList = "showId, seatNumber", unique = true),
    @Index(name = "idx_seat_status_exp", columnList = "status, lockExpiresAt")
})
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long showId;

    @Column(nullable = false, length = 16)
    private String seatNumber;

    @Column(nullable = false, length = 8)
    private String rowName;

    @Column(nullable = false)
    private int seatCol;

    @Column(nullable = false, length = 32)
    private String category; // STANDARD, PREMIUM, VIP

    @Column(nullable = false)
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SeatStatus status = SeatStatus.AVAILABLE;

    private Long lockedByUserId;

    @Column(length = 64)
    private String lockToken;

    private LocalDateTime lockExpiresAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public ShowSeat() {}

    public ShowSeat(Long showId, String seatNumber, String rowName, int seatCol, String category, double price) {
        this.showId = showId;
        this.seatNumber = seatNumber;
        this.rowName = rowName;
        this.seatCol = seatCol;
        this.category = category;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }

    public boolean isExpired(LocalDateTime now) {
        return status == SeatStatus.HELD && lockExpiresAt != null && lockExpiresAt.isBefore(now);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getRowName() { return rowName; }
    public void setRowName(String rowName) { this.rowName = rowName; }

    public int getSeatCol() { return seatCol; }
    public void setSeatCol(int seatCol) { this.seatCol = seatCol; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public Long getLockedByUserId() { return lockedByUserId; }
    public void setLockedByUserId(Long lockedByUserId) { this.lockedByUserId = lockedByUserId; }

    public String getLockToken() { return lockToken; }
    public void setLockToken(String lockToken) { this.lockToken = lockToken; }

    public LocalDateTime getLockExpiresAt() { return lockExpiresAt; }
    public void setLockExpiresAt(LocalDateTime lockExpiresAt) { this.lockExpiresAt = lockExpiresAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
