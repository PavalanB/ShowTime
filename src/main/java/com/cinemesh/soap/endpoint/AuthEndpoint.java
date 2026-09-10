package com.cinemesh.soap.endpoint;

import com.cinemesh.common.UserRole;
import com.cinemesh.model.User;
import com.cinemesh.service.AuthService;
import com.cinemesh.soap.dto.AuthSoapDto.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Optional;

@Endpoint
public class AuthEndpoint {

    private static final String NAMESPACE_URI = "http://cinemesh.com/ws/auth";
    private final AuthService authService;

    public AuthEndpoint(AuthService authService) {
        this.authService = authService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "LoginRequest")
    @ResponsePayload
    public LoginResponse login(@RequestPayload LoginRequest request) {
        LoginResponse response = new LoginResponse();
        Optional<User> userOpt = authService.authenticate(request.getUsername(), request.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = authService.generateToken(user);
            response.setStatus("SUCCESS");
            response.setToken(token);
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setRole(user.getRole().name());
            response.setTenantId(user.getTenantId());
            response.setMessage("Authentication successful");
        } else {
            response.setStatus("FAILURE");
            response.setMessage("Invalid username or password");
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "RegisterRequest")
    @ResponsePayload
    public RegisterResponse register(@RequestPayload RegisterRequest request) {
        RegisterResponse response = new RegisterResponse();
        try {
            UserRole role = UserRole.ROLE_CUSTOMER;
            if (request.getRole() != null) {
                try {
                    role = UserRole.valueOf(request.getRole());
                } catch (Exception ignored) {}
            }
            User user = authService.register(
                    request.getUsername(), request.getPassword(), request.getFullName(),
                    request.getEmail(), role, request.getTenantId()
            );
            response.setStatus("SUCCESS");
            response.setUserId(user.getId());
            response.setMessage("User registered successfully");
        } catch (Exception e) {
            response.setStatus("FAILURE");
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidateTokenRequest")
    @ResponsePayload
    public ValidateTokenResponse validateToken(@RequestPayload ValidateTokenRequest request) {
        ValidateTokenResponse response = new ValidateTokenResponse();
        boolean valid = authService.validateToken(request.getToken());
        response.setValid(valid);
        if (valid) {
            response.setUsername(authService.getUsernameFromToken(request.getToken()));
        }
        return response;
    }
}
