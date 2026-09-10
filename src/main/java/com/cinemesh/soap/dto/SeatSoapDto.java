package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class SeatSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/seat";

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SeatStatusDto {
        private Long seatId;
        private String seatNumber;
        private String rowName;
        private int seatCol;
        private String category;
        private double price;
        private String status;
        private Long lockedByUserId;
        private String lockExpiresAt;

        public Long getSeatId() { return seatId; }
        public void setSeatId(Long seatId) { this.seatId = seatId; }
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
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Long getLockedByUserId() { return lockedByUserId; }
        public void setLockedByUserId(Long lockedByUserId) { this.lockedByUserId = lockedByUserId; }
        public String getLockExpiresAt() { return lockExpiresAt; }
        public void setLockExpiresAt(String lockExpiresAt) { this.lockExpiresAt = lockExpiresAt; }
    }

    @XmlRootElement(name = "GetSeatLayoutRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetSeatLayoutRequest {
        private Long showId;
        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
    }

    @XmlRootElement(name = "GetSeatLayoutResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetSeatLayoutResponse {
        private Long showId;
        private String screenName;
        private int totalRows;
        private int seatsPerRow;
        @XmlElement(name = "seats")
        private List<SeatStatusDto> seats;

        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
        public String getScreenName() { return screenName; }
        public void setScreenName(String screenName) { this.screenName = screenName; }
        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
        public int getSeatsPerRow() { return seatsPerRow; }
        public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
        public List<SeatStatusDto> getSeats() { return seats; }
        public void setSeats(List<SeatStatusDto> seats) { this.seats = seats; }
    }

    @XmlRootElement(name = "CheckSeatAvailabilityRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CheckSeatAvailabilityRequest {
        private Long showId;
        @XmlElement(name = "seatNumbers")
        private List<String> seatNumbers;

        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
        public List<String> getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
    }

    @XmlRootElement(name = "CheckSeatAvailabilityResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CheckSeatAvailabilityResponse {
        private boolean allAvailable;
        @XmlElement(name = "availableSeats")
        private List<String> availableSeats;
        @XmlElement(name = "unavailableSeats")
        private List<String> unavailableSeats;

        public boolean isAllAvailable() { return allAvailable; }
        public void setAllAvailable(boolean allAvailable) { this.allAvailable = allAvailable; }
        public List<String> getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(List<String> availableSeats) { this.availableSeats = availableSeats; }
        public List<String> getUnavailableSeats() { return unavailableSeats; }
        public void setUnavailableSeats(List<String> unavailableSeats) { this.unavailableSeats = unavailableSeats; }
    }
}
