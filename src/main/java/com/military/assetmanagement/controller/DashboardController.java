package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import com.military.assetmanagement.dto.DashboardStatsDto;
import com.military.assetmanagement.dto.NetMovementResponseDto;
import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    public DashboardController(DashboardService dashboardService, UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) AssetCategory category,
            @RequestParam(required = false) String equipmentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        User currentUser = getCurrentUser();
        DashboardStatsDto stats = dashboardService.getDashboardStats(
                currentUser, baseId, category, equipmentType, startDate, endDate
        );
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics calculated successfully", stats));
    }

    @GetMapping("/net-movement")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<NetMovementResponseDto>> getNetMovementDetails(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) AssetCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        User currentUser = getCurrentUser();
        NetMovementResponseDto details = dashboardService.getNetMovementDetails(
                currentUser, baseId, category, startDate, endDate
        );
        return ResponseEntity.ok(ApiResponse.success("Net movement transaction details retrieved", details));
    }
}
