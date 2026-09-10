package com.cinemesh.model;

import jakarta.persistence.*;

@Entity
@Table(name = "screens", indexes = {
    @Index(name = "idx_screen_tenant", columnList = "tenantId")
})
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String tenantId;

    @Column(nullable = false)
    private int screenNumber;

    @Column(nullable = false, length = 64)
    private String screenName;

    @Column(nullable = false)
    private int totalRows = 8; // e.g. Rows A to H

    @Column(nullable = false)
    private int seatsPerRow = 10; // 1 to 10 per row

    public Screen() {}

    public Screen(String tenantId, int screenNumber, String screenName, int totalRows, int seatsPerRow) {
        this.tenantId = tenantId;
        this.screenNumber = screenNumber;
        this.screenName = screenName;
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
    }

    public int getTotalSeats() {
        return totalRows * seatsPerRow;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public int getScreenNumber() { return screenNumber; }
    public void setScreenNumber(int screenNumber) { this.screenNumber = screenNumber; }

    public String getScreenName() { return screenName; }
    public void setScreenName(String screenName) { this.screenName = screenName; }

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

    public int getSeatsPerRow() { return seatsPerRow; }
    public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
}
