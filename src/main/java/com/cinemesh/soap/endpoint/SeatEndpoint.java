package com.cinemesh.soap.endpoint;

import com.cinemesh.model.Screen;
import com.cinemesh.model.Show;
import com.cinemesh.model.ShowSeat;
import com.cinemesh.service.SeatService;
import com.cinemesh.service.ShowService;
import com.cinemesh.service.TheatreService;
import com.cinemesh.soap.dto.SeatSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class SeatEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/seat";
    private final SeatService seatService;
    private final ShowService showService;
    private final TheatreService theatreService;

    public SeatEndpoint(SeatService seatService, ShowService showService, TheatreService theatreService) {
        this.seatService = seatService;
        this.showService = showService;
        this.theatreService = theatreService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetSeatLayoutRequest")
    @ResponsePayload
    public GetSeatLayoutResponse getSeatLayout(@RequestPayload GetSeatLayoutRequest request) {
        GetSeatLayoutResponse response = new GetSeatLayoutResponse();
        response.setShowId(request.getShowId());

        Optional<Show> showOpt = showService.getShowById(request.getShowId());
        if (showOpt.isPresent()) {
            Optional<Screen> screenOpt = theatreService.getScreenById(showOpt.get().getScreenId());
            if (screenOpt.isPresent()) {
                response.setScreenName(screenOpt.get().getScreenName());
                response.setTotalRows(screenOpt.get().getTotalRows());
                response.setSeatsPerRow(screenOpt.get().getSeatsPerRow());
            }
        }

        List<SeatStatusDto> seatDtos = seatService.getSeatLayout(request.getShowId()).stream()
                .map(this::mapSeat)
                .collect(Collectors.toList());
        response.setSeats(seatDtos);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CheckSeatAvailabilityRequest")
    @ResponsePayload
    public CheckSeatAvailabilityResponse checkAvailability(@RequestPayload CheckSeatAvailabilityRequest request) {
        CheckSeatAvailabilityResponse response = new CheckSeatAvailabilityResponse();
        boolean allAvail = seatService.areSeatsAvailable(request.getShowId(), request.getSeatNumbers());
        response.setAllAvailable(allAvail);

        List<ShowSeat> currentSeats = seatService.getSeatLayout(request.getShowId());
        List<String> avail = new ArrayList<>();
        List<String> unavail = new ArrayList<>();

        for (ShowSeat s : currentSeats) {
            if (request.getSeatNumbers().contains(s.getSeatNumber())) {
                if (s.getStatus() == com.cinemesh.common.SeatStatus.AVAILABLE) {
                    avail.add(s.getSeatNumber());
                } else {
                    unavail.add(s.getSeatNumber());
                }
            }
        }
        response.setAvailableSeats(avail);
        response.setUnavailableSeats(unavail);
        return response;
    }

    private SeatStatusDto mapSeat(ShowSeat s) {
        SeatStatusDto dto = new SeatStatusDto();
        dto.setSeatId(s.getId());
        dto.setSeatNumber(s.getSeatNumber());
        dto.setRowName(s.getRowName());
        dto.setSeatCol(s.getSeatCol());
        dto.setCategory(s.getCategory());
        dto.setPrice(s.getPrice());
        dto.setStatus(s.getStatus().name());
        dto.setLockedByUserId(s.getLockedByUserId());
        dto.setLockExpiresAt(s.getLockExpiresAt() != null ? s.getLockExpiresAt().toString() : null);
        return dto;
    }
}
