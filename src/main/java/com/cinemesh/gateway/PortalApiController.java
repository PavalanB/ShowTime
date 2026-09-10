package com.cinemesh.gateway;

import com.cinemesh.common.SeatStatus;
import com.cinemesh.model.*;
import com.cinemesh.repository.*;
import com.cinemesh.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PortalApiController {

    private final TenantService tenantService;
    private final MovieService movieService;
    private final ShowService showService;
    private final SeatService seatService;
    private final CoordinationService coordinationService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final TicketService ticketService;
    private final AnalyticsService analyticsService;
    private final AuthService authService;
    private final TheatreService theatreService;

    @Value("${cinemesh.azure.vnet-name:vnet-cinemesh-prod}")
    private String vnetName;

    public PortalApiController(TenantService tenantService,
                               MovieService movieService,
                               ShowService showService,
                               SeatService seatService,
                               CoordinationService coordinationService,
                               BookingService bookingService,
                               PaymentService paymentService,
                               TicketService ticketService,
                               AnalyticsService analyticsService,
                               AuthService authService,
                               TheatreService theatreService) {
        this.tenantService = tenantService;
        this.movieService = movieService;
        this.showService = showService;
        this.seatService = seatService;
        this.coordinationService = coordinationService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.ticketService = ticketService;
        this.analyticsService = analyticsService;
        this.authService = authService;
        this.theatreService = theatreService;
    }

    // --- Auth ---
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Optional<com.cinemesh.model.User> userOpt = authService.authenticate(username, password);
        
        if (userOpt.isPresent()) {
            com.cinemesh.model.User user = userOpt.get();
            String token = authService.generateToken(user);
            Map<String, Object> resp = new HashMap<>();
            resp.put("token", token);
            resp.put("userId", user.getId());
            resp.put("role", user.getRole().name());
            resp.put("tenantId", user.getTenantId());
            resp.put("fullName", user.getFullName());
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String fullName = body.get("fullName");
            String email = body.get("email");
            String roleStr = body.get("role");
            
            com.cinemesh.common.UserRole role = com.cinemesh.common.UserRole.valueOf(roleStr);
            String tenantId = null;

            if (role == com.cinemesh.common.UserRole.ROLE_THEATRE_OWNER) {
                String theatreName = body.get("theatreName");
                String theatreCity = body.get("theatreCity");
                tenantId = "tenant-" + System.currentTimeMillis();
                tenantService.createTenant(tenantId, theatreName, theatreCity, "", email);
                theatreService.createScreen(tenantId, 1, "Standard Screen", 6, 8);
                theatreService.createScreen(tenantId, 2, "Premium Screen", 6, 8);
            }

            com.cinemesh.model.User user = authService.register(username, password, fullName, email, role, tenantId);
            return ResponseEntity.ok(Map.of("success", true, "userId", user.getId(), "tenantId", tenantId == null ? "" : tenantId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Movies (Owner) ---
    @PostMapping("/movies")
    public ResponseEntity<?> createMovie(@RequestBody Map<String, Object> body) {
        try {
            Movie movie = movieService.addMovie(
                (String) body.get("title"),
                (String) body.get("description"),
                Integer.parseInt(body.get("durationMinutes").toString()),
                (String) body.get("genre"),
                (String) body.get("language"),
                (String) body.get("posterUrl"),
                (String) body.get("rating")
            );
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/movies/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Movie movie = movieService.updateMovie(
                id,
                (String) body.get("title"),
                (String) body.get("description"),
                Integer.parseInt(body.get("durationMinutes").toString()),
                (String) body.get("genre"),
                (String) body.get("language"),
                (String) body.get("posterUrl"),
                (String) body.get("rating")
            );
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Shows (Owner) ---
    @PostMapping("/shows")
    public ResponseEntity<?> scheduleShow(@RequestBody Map<String, Object> body) {
        try {
            String tenantId = (String) body.get("tenantId");
            Long movieId = Long.valueOf(body.get("movieId").toString());
            Long screenId = Long.valueOf(body.get("screenId").toString());
            java.time.LocalDateTime startTime = java.time.LocalDateTime.parse((String) body.get("startTime"));
            double standardPrice = Double.parseDouble(body.get("standardPrice").toString());
            double premiumPrice = Double.parseDouble(body.get("premiumPrice").toString());
            double vipPrice = Double.parseDouble(body.get("vipPrice").toString());
            
            Show show = showService.scheduleShow(tenantId, movieId, screenId, startTime, standardPrice, premiumPrice, vipPrice);
            return ResponseEntity.ok(show);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/shows/{id}")
    public ResponseEntity<?> updateShow(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            java.time.LocalDateTime startTime = java.time.LocalDateTime.parse((String) body.get("startTime"));
            double standardPrice = Double.parseDouble(body.get("standardPrice").toString());
            double premiumPrice = Double.parseDouble(body.get("premiumPrice").toString());
            double vipPrice = Double.parseDouble(body.get("vipPrice").toString());
            
            Show show = showService.updateShow(id, startTime, standardPrice, premiumPrice, vipPrice);
            return ResponseEntity.ok(show);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Tenants ---
    @GetMapping("/tenants")
    public List<Tenant> listTenants() {
        return tenantService.listAllTenants();
    }

    @GetMapping("/tenants/{tenantId}")
    public ResponseEntity<Tenant> getTenant(@PathVariable String tenantId) {
        return tenantService.getTenant(tenantId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // --- Movies ---
    @GetMapping("/movies")
    public List<Movie> listMovies() {
        return movieService.getAllMovies();
    }

    // --- Shows ---
    @GetMapping("/shows")
    public List<Map<String, Object>> listShows(@RequestParam(required = false) String tenantId) {
        boolean filterTenant = tenantId != null && !tenantId.isEmpty() && !tenantId.equalsIgnoreCase("ALL") && !tenantId.equalsIgnoreCase("all");
        List<Show> shows = filterTenant
                ? showService.getShowsByTenant(tenantId)
                : showService.getAllShows();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Show s : shows) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("tenantId", s.getTenantId());
            map.put("movieId", s.getMovieId());
            map.put("screenId", s.getScreenId());
            map.put("startTime", s.getStartTime());
            map.put("endTime", s.getEndTime());
            map.put("standardPrice", s.getStandardPrice());
            map.put("premiumPrice", s.getPremiumPrice());
            map.put("vipPrice", s.getVipPrice());

            tenantService.getTenant(s.getTenantId()).ifPresent(t -> {
                map.put("theatreName", t.getName());
                map.put("theatreCity", t.getCity());
                map.put("theatreFullName", t.getName() + " (" + t.getCity() + ")");
            });
            movieService.getMovieById(s.getMovieId()).ifPresent(m -> {
                map.put("movieTitle", m.getTitle());
                map.put("posterUrl", m.getPosterUrl());
                map.put("genre", m.getGenre());
                map.put("language", m.getLanguage());
                map.put("rating", m.getRating());
                map.put("durationMinutes", m.getDurationMinutes());
            });
            theatreService.getScreenById(s.getScreenId()).ifPresentOrElse(
                sc -> map.put("screenName", sc.getScreenName()),
                () -> map.put("screenName", "Screen " + s.getScreenId())
            );
            result.add(map);
        }
        return result;
    }

    @DeleteMapping("/shows/{id}")
    public ResponseEntity<?> deleteShow(@PathVariable Long id) {
        try {
            showService.deleteShow(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Seats ---
    @GetMapping("/shows/{showId}/seats")
    public ResponseEntity<Map<String, Object>> getSeats(@PathVariable Long showId) {
        Optional<Show> showOpt = showService.getShowById(showId);
        if (showOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Show show = showOpt.get();
        Screen screen = theatreService.getScreenById(show.getScreenId()).orElse(null);
        List<ShowSeat> seats = seatService.getSeatLayout(showId);

        Map<String, Object> response = new HashMap<>();
        response.put("showId", showId);
        response.put("screenName", screen != null ? screen.getScreenName() : "Main Screen");
        response.put("totalRows", screen != null ? screen.getTotalRows() : 8);
        response.put("seatsPerRow", screen != null ? screen.getSeatsPerRow() : 10);
        response.put("seats", seats);
        return ResponseEntity.ok(response);
    }

    // --- Seat Locking (Distributed Concurrency) ---
    @PostMapping("/shows/{showId}/lock")
    public ResponseEntity<?> lockSeats(@PathVariable Long showId, @RequestBody Map<String, Object> body) {
        Long customerId = Long.valueOf(body.getOrDefault("customerId", 1).toString());
        List<String> seats = (List<String>) body.get("seats");
        int timeoutSeconds = Integer.parseInt(body.getOrDefault("timeoutSeconds", 300).toString());

        CoordinationService.LockResult result = coordinationService.acquireSeatLock(showId, customerId, seats, timeoutSeconds);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(409).body(result);
        }
    }

    @PostMapping("/shows/{showId}/unlock")
    public ResponseEntity<?> unlockSeats(@PathVariable Long showId, @RequestBody Map<String, Object> body) {
        String lockToken = (String) body.get("lockToken");
        Long customerId = Long.valueOf(body.getOrDefault("customerId", 1).toString());
        boolean released = coordinationService.releaseSeatLock(showId, lockToken, customerId);
        return ResponseEntity.ok(Map.of("released", released));
    }

    // --- Bookings ---
    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> body) {
        try {
            Long showId = Long.valueOf(body.get("showId").toString());
            Long customerId = Long.valueOf(body.getOrDefault("customerId", 1).toString());
            String lockToken = (String) body.get("lockToken");
            List<String> seats = (List<String>) body.get("seats");

            Booking booking = bookingService.createBooking(showId, customerId, lockToken, seats);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bookings/confirm")
    public ResponseEntity<?> confirmBooking(@RequestBody Map<String, Object> body) {
        try {
            String bookingRef = (String) body.get("bookingReference");
            String paymentMethod = (String) body.getOrDefault("paymentMethod", "CARD");
            boolean simulateFailure = Boolean.parseBoolean(body.getOrDefault("simulateFailure", false).toString());
            double amount = Double.parseDouble(body.get("amount").toString());

            Payment payment = paymentService.processPayment(bookingRef, amount, paymentMethod, simulateFailure);
            if (payment.getStatus() != com.cinemesh.common.PaymentStatus.SUCCESS) {
                bookingService.cancelBooking(bookingRef);
                return ResponseEntity.status(402).body(Map.of(
                        "status", "FAILED",
                        "message", "Payment processing failed. Reserved seats have been released."
                ));
            }

            Booking confirmed = bookingService.confirmBooking(bookingRef, payment.getTransactionId());
            Optional<Ticket> ticketOpt = ticketService.getTicketByBookingReference(bookingRef);

            Map<String, Object> resp = new HashMap<>();
            resp.put("status", "SUCCESS");
            resp.put("booking", confirmed);
            resp.put("payment", payment);
            ticketOpt.ifPresent(t -> resp.put("ticket", t));
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bookings/{bookingRef}")
    public ResponseEntity<?> getBooking(@PathVariable String bookingRef) {
        Optional<Booking> booking = bookingService.getBookingByReference(bookingRef);
        if (booking.isEmpty()) return ResponseEntity.notFound().build();

        Optional<Ticket> ticket = ticketService.getTicketByBookingReference(bookingRef);
        Map<String, Object> resp = new HashMap<>();
        resp.put("booking", booking.get());
        ticket.ifPresent(t -> resp.put("ticket", t));
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/bookings/customer/{customerId}")
    public List<Map<String, Object>> getCustomerBookings(@PathVariable Long customerId) {
        List<Booking> bookings = bookingService.getBookingsByCustomer(customerId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Booking b : bookings) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("bookingReference", b.getBookingReference());
            map.put("showId", b.getShowId());
            map.put("tenantId", b.getTenantId());
            map.put("seatsCsv", b.getSeatsCsv());
            map.put("totalAmount", b.getTotalAmount());
            map.put("status", b.getStatus().name());
            map.put("paymentTransactionId", b.getPaymentTransactionId());

            showService.getShowById(b.getShowId()).ifPresent(s -> {
                map.put("showTime", s.getStartTime());
                movieService.getMovieById(s.getMovieId()).ifPresent(m -> {
                    map.put("movieTitle", m.getTitle());
                    map.put("posterUrl", m.getPosterUrl());
                    map.put("language", m.getLanguage());
                    map.put("genre", m.getGenre());
                });
                theatreService.getScreenById(s.getScreenId()).ifPresent(sc -> map.put("screenName", sc.getScreenName()));
            });

            tenantService.getTenant(b.getTenantId()).ifPresent(t -> {
                map.put("theatreName", t.getName());
                map.put("theatreCity", t.getCity());
                map.put("theatreFullName", t.getName() + " (" + t.getCity() + ")");
            });

            ticketService.getTicketByBookingReference(b.getBookingReference()).ifPresent(t -> {
                map.put("ticketReference", t.getTicketReference());
                map.put("validationCode", t.getValidationCode());
                map.put("ticketStatus", t.getStatus().name());
            });

            result.add(map);
        }
        return result;
    }

    // --- Ticket Validation Scanner ---
    @PostMapping("/tickets/validate")
    public ResponseEntity<?> validateTicket(@RequestBody Map<String, String> body) {
        String ticketRef = body.get("ticketReference");
        boolean valid = ticketService.validateTicket(ticketRef);
        Optional<Ticket> ticket = ticketService.getTicketByReference(ticketRef);

        Map<String, Object> resp = new HashMap<>();
        resp.put("valid", valid);
        resp.put("status", valid ? "VALIDATED" : "INVALID_OR_ALREADY_USED");
        ticket.ifPresent(t -> resp.put("ticket", t));
        return ResponseEntity.ok(resp);
    }

    // --- Analytics ---
    @GetMapping("/analytics/{tenantId}")
    public ResponseEntity<?> getAnalytics(@PathVariable String tenantId) {
        return ResponseEntity.ok(analyticsService.getTenantReport(tenantId));
    }

    // --- Azure Cloud Topology & Monitoring ---
    @GetMapping("/cloud/topology")
    public Map<String, Object> getCloudTopology() {
        Map<String, Object> topology = new LinkedHashMap<>();
        topology.put("vnet", vnetName);
        topology.put("environment", "Microsoft Azure Simulated VNet (East US)");
        topology.put("distributedProtocols", Arrays.asList("SOAP 1.1 / WSDL", "XML over HTTP", "WebSocket STOMP"));
        topology.put("securityModel", "Spring Security RBAC + Tenant Isolation Context");
        topology.put("concurrencyModel", "Distributed Pessimistic/Optimistic Seat Locks with Auto-Reaping TTL");

        List<Map<String, Object>> nodes = new ArrayList<>();

        nodes.add(Map.of(
                "node", "vm-gateway",
                "role", "Edge API Gateway, Reverse Proxy & Web UI",
                "ip", "10.0.1.4",
                "subnet", "snet-gateway (10.0.1.0/24)",
                "port", 8080,
                "status", "HEALTHY",
                "services", Arrays.asList("Web Portal", "SOAP Client Gateway", "WebSocket Engine")
        ));
        nodes.add(Map.of(
                "node", "vm-identity",
                "role", "Tenant Isolation & Identity Provider",
                "ip", "10.0.2.4",
                "subnet", "snet-identity (10.0.2.0/24)",
                "port", 8081,
                "status", "HEALTHY",
                "services", Arrays.asList("AuthService (/ws/auth.wsdl)", "TenantManagementService (/ws/tenants.wsdl)")
        ));
        nodes.add(Map.of(
                "node", "vm-catalog",
                "role", "Theatre & Show Scheduling Node",
                "ip", "10.0.3.4",
                "subnet", "snet-catalog (10.0.3.0/24)",
                "port", 8082,
                "status", "HEALTHY",
                "services", Arrays.asList("TheatreService (/ws/theatres.wsdl)", "MovieService (/ws/movies.wsdl)", "ShowService (/ws/shows.wsdl)")
        ));
        nodes.add(Map.of(
                "node", "vm-booking",
                "role", "Core Transactions & Distributed Lock Node",
                "ip", "10.0.3.5",
                "subnet", "snet-booking (10.0.3.0/24)",
                "port", 8083,
                "status", "HEALTHY",
                "services", Arrays.asList("SeatService (/ws/seats.wsdl)", "CoordinationService (/ws/coordination.wsdl)", "BookingService (/ws/booking.wsdl)")
        ));
        nodes.add(Map.of(
                "node", "vm-fulfillment",
                "role", "Fulfillment, Ticketing & Analytics Node",
                "ip", "10.0.4.4",
                "subnet", "snet-fulfillment (10.0.4.0/24)",
                "port", 8084,
                "status", "HEALTHY",
                "services", Arrays.asList("PaymentService (/ws/payment.wsdl)", "TicketService (/ws/ticket.wsdl)", "NotificationService (/ws/notification.wsdl)", "AnalyticsService (/ws/analytics.wsdl)")
        ));

        topology.put("nodes", nodes);
        return topology;
    }
}
