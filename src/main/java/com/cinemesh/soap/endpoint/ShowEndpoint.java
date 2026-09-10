package com.cinemesh.soap.endpoint;

import com.cinemesh.model.Movie;
import com.cinemesh.model.Screen;
import com.cinemesh.model.Show;
import com.cinemesh.model.Tenant;
import com.cinemesh.service.MovieService;
import com.cinemesh.service.ShowService;
import com.cinemesh.service.TenantService;
import com.cinemesh.service.TheatreService;
import com.cinemesh.soap.dto.ShowSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class ShowEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/show";
    private final ShowService showService;
    private final MovieService movieService;
    private final TheatreService theatreService;
    private final TenantService tenantService;

    public ShowEndpoint(ShowService showService, MovieService movieService, TheatreService theatreService, TenantService tenantService) {
        this.showService = showService;
        this.movieService = movieService;
        this.theatreService = theatreService;
        this.tenantService = tenantService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ScheduleShowRequest")
    @ResponsePayload
    public ScheduleShowResponse scheduleShow(@RequestPayload ScheduleShowRequest request) {
        ScheduleShowResponse response = new ScheduleShowResponse();
        try {
            LocalDateTime startTime = LocalDateTime.parse(request.getStartTime());
            Show show = showService.scheduleShow(
                    request.getTenantId(), request.getMovieId(), request.getScreenId(),
                    startTime, request.getStandardPrice(), request.getPremiumPrice(), request.getVipPrice()
            );
            response.setStatus("SUCCESS");
            response.setShow(mapShow(show));
            response.setMessage("Show scheduled and seats initialized successfully");
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetShowsByTenantRequest")
    @ResponsePayload
    public GetShowsByTenantResponse getShows(@RequestPayload GetShowsByTenantRequest request) {
        GetShowsByTenantResponse response = new GetShowsByTenantResponse();
        List<ShowDto> list = showService.getShowsByTenant(request.getTenantId()).stream()
                .map(this::mapShow)
                .collect(Collectors.toList());
        response.setShows(list);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetShowDetailsRequest")
    @ResponsePayload
    public GetShowDetailsResponse getShowDetails(@RequestPayload GetShowDetailsRequest request) {
        GetShowDetailsResponse response = new GetShowDetailsResponse();
        Optional<Show> showOpt = showService.getShowById(request.getShowId());
        if (showOpt.isPresent()) {
            response.setStatus("SUCCESS");
            response.setShow(mapShow(showOpt.get()));
        } else {
            response.setStatus("NOT_FOUND");
        }
        return response;
    }

    private ShowDto mapShow(Show s) {
        ShowDto dto = new ShowDto();
        dto.setShowId(s.getId());
        dto.setTenantId(s.getTenantId());
        dto.setMovieId(s.getMovieId());
        dto.setScreenId(s.getScreenId());
        dto.setStartTime(s.getStartTime().toString());
        dto.setEndTime(s.getEndTime() != null ? s.getEndTime().toString() : "");

        tenantService.getTenant(s.getTenantId()).ifPresent(t -> dto.setTheatreName(t.getName()));
        movieService.getMovieById(s.getMovieId()).ifPresent(m -> dto.setMovieTitle(m.getTitle()));
        theatreService.getScreenById(s.getScreenId()).ifPresent(sc -> dto.setScreenName(sc.getScreenName()));

        List<ShowPricingDto> pricing = new ArrayList<>();
        pricing.add(new ShowPricingDto("STANDARD", s.getStandardPrice()));
        pricing.add(new ShowPricingDto("PREMIUM", s.getPremiumPrice()));
        pricing.add(new ShowPricingDto("VIP", s.getVipPrice()));
        dto.setPricing(pricing);

        return dto;
    }
}
