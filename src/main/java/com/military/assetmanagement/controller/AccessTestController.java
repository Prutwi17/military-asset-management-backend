package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/access-test")
public class AccessTestController {

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> adminOnly() {
        return ResponseEntity.ok(ApiResponse.success(
                "Authorized: Admin access granted",
                Map.of("level", "ADMIN_CONFIDENTIAL", "message", "Welcome to Admin Command Console")
        ));
    }

    @GetMapping("/commander-only")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> commanderAccess() {
        return ResponseEntity.ok(ApiResponse.success(
                "Authorized: Commander access granted",
                Map.of("level", "BASE_COMMAND", "message", "Welcome to Base Commander Operations")
        ));
    }

    @GetMapping("/logistics-only")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> logisticsAccess() {
        return ResponseEntity.ok(ApiResponse.success(
                "Authorized: Logistics access granted",
                Map.of("level", "LOGISTICS_SUPPLY", "message", "Welcome to Logistics Operations")
        ));
    }
}
