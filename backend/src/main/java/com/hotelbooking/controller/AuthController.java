package com.hotelbooking.controller;

import com.hotelbooking.dto.auth.AuthResponse;
import com.hotelbooking.dto.auth.AuthResult;
import com.hotelbooking.dto.auth.LoginRequest;
import com.hotelbooking.dto.auth.RegisterRequest;
import com.hotelbooking.dto.auth.UserStatusUpdateRequest;
import com.hotelbooking.dto.auth.UserResponse;
import com.hotelbooking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${app.jwt.cookie-name}")
    private String jwtCookieName;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.cookie-secure}")
    private boolean cookieSecure;

    @Value("${app.jwt.cookie-same-site}")
    private String cookieSameSite;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = authService.login(request.getEmail(), request.getPassword());
        ResponseCookie cookie = buildAuthCookie(result.getToken(), jwtExpirationMs);

        AuthResponse response = AuthResponse.builder()
                .message(result.getMessage())
                .username(result.getUsername())
                .role(result.getRole())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @Operation(summary = "Logout current user")
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout() {
        ResponseCookie cookie = buildAuthCookie("", 0);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(AuthResponse.builder()
                        .message("Logout successful")
                        .build());
    }

    @Operation(summary = "Get authenticated user details")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }

    @Operation(summary = "Get all active users")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> allUsers() {
        return ResponseEntity.ok(authService.getAllUsersForAdmin());
    }

    @Operation(summary = "Update user active status")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(authService.updateUserStatus(userId, request.getIsActive(), authentication.getName()));
    }

    private ResponseCookie buildAuthCookie(String token, long maxAgeMs) {
        return ResponseCookie.from(jwtCookieName, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(Duration.ofMillis(maxAgeMs))
                .build();
    }
}
