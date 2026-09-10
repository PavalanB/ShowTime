package com.cinemesh.config;

import com.cinemesh.common.UserRole;
import com.cinemesh.model.*;
import com.cinemesh.repository.*;
import com.cinemesh.service.AuthService;
import com.cinemesh.service.ShowService;
import com.cinemesh.service.TenantService;
import com.cinemesh.service.TheatreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final AuthService authService;
    private final TenantService tenantService;
    private final TheatreService theatreService;
    private final ShowService showService;

    public DataInitializer(TenantRepository tenantRepository,
                           UserRepository userRepository,
                           ScreenRepository screenRepository,
                           MovieRepository movieRepository,
                           ShowRepository showRepository,
                           AuthService authService,
                           TenantService tenantService,
                           TheatreService theatreService,
                           ShowService showService) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.screenRepository = screenRepository;
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
        this.authService = authService;
        this.tenantService = tenantService;
        this.theatreService = theatreService;
        this.showService = showService;
    }

    @Override
    public void run(String... args) {
        log.info("CineMesh database initialization skipped. Ready for dynamic registrations via MySQL.");
    }
}
