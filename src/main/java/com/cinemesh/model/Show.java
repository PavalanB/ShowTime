package com.cinemesh.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shows", indexes = {
    @Index(name = "idx_show_tenant", columnList = "tenantId"),
    @Index(name = "idx_show_movie", columnList = "movieId"),
    @Index(name = "idx_show_screen", columnList = "screenId")
})
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String tenantId;

    @Column(nullable = false)
    private Long movieId;

    @Column(nullable = false)
    private Long screenId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(nullable = false)
    private double standardPrice = 200.0;

    @Column(nullable = false)
    private double premiumPrice = 350.0;

    @Column(nullable = false)
    private double vipPrice = 500.0;

    public Show() {}

    public Show(String tenantId, Long movieId, Long screenId, LocalDateTime startTime, double standardPrice, double premiumPrice, double vipPrice) {
        this.tenantId = tenantId;
        this.movieId = movieId;
        this.screenId = screenId;
        this.startTime = startTime;
        this.endTime = startTime.plusHours(2).plusMinutes(30);
        this.standardPrice = standardPrice;
        this.premiumPrice = premiumPrice;
        this.vipPrice = vipPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Long getScreenId() { return screenId; }
    public void setScreenId(Long screenId) { this.screenId = screenId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public double getStandardPrice() { return standardPrice; }
    public void setStandardPrice(double standardPrice) { this.standardPrice = standardPrice; }

    public double getPremiumPrice() { return premiumPrice; }
    public void setPremiumPrice(double premiumPrice) { this.premiumPrice = premiumPrice; }

    public double getVipPrice() { return vipPrice; }
    public void setVipPrice(double vipPrice) { this.vipPrice = vipPrice; }
}
