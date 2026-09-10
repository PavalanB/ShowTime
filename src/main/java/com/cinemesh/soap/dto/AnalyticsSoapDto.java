package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;

public class AnalyticsSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/analytics";

    @XmlRootElement(name = "GetTenantAnalyticsRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetTenantAnalyticsRequest {
        private String tenantId;
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    }

    @XmlRootElement(name = "GetTenantAnalyticsResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetTenantAnalyticsResponse {
        private String tenantId;
        private String theatreName;
        private long totalBookings;
        private long totalTicketsSold;
        private double totalRevenue;
        private double averageOccupancyRate;
        private int activeScreens;
        private int scheduledShows;

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getTheatreName() { return theatreName; }
        public void setTheatreName(String theatreName) { this.theatreName = theatreName; }
        public long getTotalBookings() { return totalBookings; }
        public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }
        public long getTotalTicketsSold() { return totalTicketsSold; }
        public void setTotalTicketsSold(long totalTicketsSold) { this.totalTicketsSold = totalTicketsSold; }
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public double getAverageOccupancyRate() { return averageOccupancyRate; }
        public void setAverageOccupancyRate(double averageOccupancyRate) { this.averageOccupancyRate = averageOccupancyRate; }
        public int getActiveScreens() { return activeScreens; }
        public void setActiveScreens(int activeScreens) { this.activeScreens = activeScreens; }
        public int getScheduledShows() { return scheduledShows; }
        public void setScheduledShows(int scheduledShows) { this.scheduledShows = scheduledShows; }
    }
}
