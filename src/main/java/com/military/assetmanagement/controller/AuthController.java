package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import com.military.assetmanagement.dto.AuthResponse;
import com.military.assetmanagement.dto.LoginRequest;
import com.military.assetmanagement.dto.UserSummaryDto;
import com.military.assetmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        String username = authentication.getName();
        UserSummaryDto user = authService.getCurrentUser(username);
        return ResponseEntity.ok(ApiResponse.success("User profile fetched successfully", user));
    }
}
