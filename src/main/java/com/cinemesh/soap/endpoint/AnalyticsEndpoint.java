package com.cinemesh.soap.endpoint;

import com.cinemesh.service.AnalyticsService;
import com.cinemesh.service.AnalyticsService.TenantAnalyticsReport;
import com.cinemesh.soap.dto.AnalyticsSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class AnalyticsEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/analytics";
    private final AnalyticsService analyticsService;

    public AnalyticsEndpoint(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetTenantAnalyticsRequest")
    @ResponsePayload
    public GetTenantAnalyticsResponse getAnalytics(@RequestPayload GetTenantAnalyticsRequest request) {
        GetTenantAnalyticsResponse response = new GetTenantAnalyticsResponse();
        TenantAnalyticsReport report = analyticsService.getTenantReport(request.getTenantId());

        response.setTenantId(report.getTenantId());
        response.setTheatreName(report.getTheatreName());
        response.setTotalBookings(report.getTotalBookings());
        response.setTotalTicketsSold(report.getTotalTicketsSold());
        response.setTotalRevenue(report.getTotalRevenue());
        response.setAverageOccupancyRate(report.getAverageOccupancyRate());
        response.setActiveScreens(report.getActiveScreens());
        response.setScheduledShows(report.getScheduledShows());
        return response;
    }
}
