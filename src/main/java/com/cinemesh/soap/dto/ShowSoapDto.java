package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class ShowSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/show";

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ShowPricingDto {
        private String category;
        private double price;

        public ShowPricingDto() {}
        public ShowPricingDto(String category, double price) {
            this.category = category;
            this.price = price;
        }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ShowDto {
        private Long showId;
        private String tenantId;
        private String theatreName;
        private Long movieId;
        private String movieTitle;
        private Long screenId;
        private String screenName;
        private String startTime;
        private String endTime;
        @XmlElement(name = "pricing")
        private List<ShowPricingDto> pricing;

        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getTheatreName() { return theatreName; }
        public void setTheatreName(String theatreName) { this.theatreName = theatreName; }
        public Long getMovieId() { return movieId; }
        public void setMovieId(Long movieId) { this.movieId = movieId; }
        public String getMovieTitle() { return movieTitle; }
        public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
        public Long getScreenId() { return screenId; }
        public void setScreenId(Long screenId) { this.screenId = screenId; }
        public String getScreenName() { return screenName; }
        public void setScreenName(String screenName) { this.screenName = screenName; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public List<ShowPricingDto> getPricing() { return pricing; }
        public void setPricing(List<ShowPricingDto> pricing) { this.pricing = pricing; }
    }

    @XmlRootElement(name = "ScheduleShowRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ScheduleShowRequest {
        private String tenantId;
        private Long movieId;
        private Long screenId;
        private String startTime;
        private double standardPrice;
        private double premiumPrice;
        private double vipPrice;

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public Long getMovieId() { return movieId; }
        public void setMovieId(Long movieId) { this.movieId = movieId; }
        public Long getScreenId() { return screenId; }
        public void setScreenId(Long screenId) { this.screenId = screenId; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public double getStandardPrice() { return standardPrice; }
        public void setStandardPrice(double standardPrice) { this.standardPrice = standardPrice; }
        public double getPremiumPrice() { return premiumPrice; }
        public void setPremiumPrice(double premiumPrice) { this.premiumPrice = premiumPrice; }
        public double getVipPrice() { return vipPrice; }
        public void setVipPrice(double vipPrice) { this.vipPrice = vipPrice; }
    }

    @XmlRootElement(name = "ScheduleShowResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ScheduleShowResponse {
        private String status;
        private ShowDto show;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public ShowDto getShow() { return show; }
        public void setShow(ShowDto show) { this.show = show; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "GetShowsByTenantRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetShowsByTenantRequest {
        private String tenantId;
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    }

    @XmlRootElement(name = "GetShowsByTenantResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetShowsByTenantResponse {
        @XmlElement(name = "shows")
        private List<ShowDto> shows;

        public List<ShowDto> getShows() { return shows; }
        public void setShows(List<ShowDto> shows) { this.shows = shows; }
    }

    @XmlRootElement(name = "GetShowDetailsRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetShowDetailsRequest {
        private Long showId;
        public Long getShowId() { return showId; }
        public void setShowId(Long showId) { this.showId = showId; }
    }

    @XmlRootElement(name = "GetShowDetailsResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetShowDetailsResponse {
        private String status;
        private ShowDto show;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public ShowDto getShow() { return show; }
        public void setShow(ShowDto show) { this.show = show; }
    }
}
