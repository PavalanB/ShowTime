package com.cinemesh.soap.endpoint;

import com.cinemesh.model.Screen;
import com.cinemesh.service.TheatreService;
import com.cinemesh.soap.dto.TheatreSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class TheatreEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/theatre";
    private final TheatreService theatreService;

    public TheatreEndpoint(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateScreenRequest")
    @ResponsePayload
    public CreateScreenResponse createScreen(@RequestPayload CreateScreenRequest request) {
        CreateScreenResponse response = new CreateScreenResponse();
        try {
            Screen screen = theatreService.createScreen(
                    request.getTenantId(), request.getScreenNumber(), request.getScreenName(),
                    request.getTotalRows(), request.getSeatsPerRow()
            );
            response.setStatus("SUCCESS");
            response.setScreen(mapScreen(screen));
            response.setMessage("Screen created successfully");
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetScreensByTenantRequest")
    @ResponsePayload
    public GetScreensByTenantResponse getScreens(@RequestPayload GetScreensByTenantRequest request) {
        GetScreensByTenantResponse response = new GetScreensByTenantResponse();
        List<ScreenDto> screens = theatreService.getScreensByTenant(request.getTenantId()).stream()
                .map(this::mapScreen)
                .collect(Collectors.toList());
        response.setScreens(screens);
        return response;
    }

    private ScreenDto mapScreen(Screen s) {
        ScreenDto dto = new ScreenDto();
        dto.setScreenId(s.getId());
        dto.setScreenNumber(s.getScreenNumber());
        dto.setScreenName(s.getScreenName());
        dto.setTotalRows(s.getTotalRows());
        dto.setSeatsPerRow(s.getSeatsPerRow());
        dto.setTotalSeats(s.getTotalSeats());
        dto.setTenantId(s.getTenantId());
        return dto;
    }
}
