package com.cinemesh.soap.dto;

import jakarta.xml.bind.annotation.*;
import java.util.List;

public class TheatreSoapDto {
    public static final String NAMESPACE_URI = "http://cinemesh.com/ws/theatre";

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ScreenDto {
        private Long screenId;
        private int screenNumber;
        private String screenName;
        private int totalRows;
        private int seatsPerRow;
        private int totalSeats;
        private String tenantId;

        public Long getScreenId() { return screenId; }
        public void setScreenId(Long screenId) { this.screenId = screenId; }
        public int getScreenNumber() { return screenNumber; }
        public void setScreenNumber(int screenNumber) { this.screenNumber = screenNumber; }
        public String getScreenName() { return screenName; }
        public void setScreenName(String screenName) { this.screenName = screenName; }
        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
        public int getSeatsPerRow() { return seatsPerRow; }
        public void setSeatsPerRow(int seatsPerRow) { this.seatsPerRow = seatsPerRow; }
        public int getTotalSeats() { return totalSeats; }
        public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    }

    @XmlRootElement(name = "CreateScreenRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreateScreenRequest {
        private String tenantId;
        private int screenNumber;
        private String screenName;
        private int totalRows;
        private int seatsPerRow;

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

    @XmlRootElement(name = "CreateScreenResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CreateScreenResponse {
        private String status;
        private ScreenDto screen;
        private String message;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public ScreenDto getScreen() { return screen; }
        public void setScreen(ScreenDto screen) { this.screen = screen; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "GetScreensByTenantRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetScreensByTenantRequest {
        private String tenantId;
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    }

    @XmlRootElement(name = "GetScreensByTenantResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetScreensByTenantResponse {
        @XmlElement(name = "screens")
        private List<ScreenDto> screens;

        public List<ScreenDto> getScreens() { return screens; }
        public void setScreens(List<ScreenDto> screens) { this.screens = screens; }
    }
}
