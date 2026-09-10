package com.cinemesh.service;

import com.cinemesh.model.Screen;
import com.cinemesh.repository.ScreenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TheatreService {

    private final ScreenRepository screenRepository;

    public TheatreService(ScreenRepository screenRepository) {
        this.screenRepository = screenRepository;
    }

    public Screen createScreen(String tenantId, int screenNumber, String screenName, int totalRows, int seatsPerRow) {
        Optional<Screen> existing = screenRepository.findByTenantIdAndScreenNumber(tenantId, screenNumber);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Screen number " + screenNumber + " already exists for tenant " + tenantId);
        }
        Screen screen = new Screen(tenantId, screenNumber, screenName, totalRows, seatsPerRow);
        return screenRepository.save(screen);
    }

    public List<Screen> getScreensByTenant(String tenantId) {
        return screenRepository.findByTenantId(tenantId);
    }

    public Optional<Screen> getScreenById(Long screenId) {
        return screenRepository.findById(screenId);
    }
}
