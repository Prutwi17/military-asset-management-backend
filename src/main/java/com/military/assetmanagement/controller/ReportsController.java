package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import com.military.assetmanagement.dto.ReportsDto;
import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.service.ReportsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;
    private final UserRepository userRepository;

    public ReportsController(ReportsService reportsService, UserRepository userRepository) {
        this.reportsService = reportsService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<ReportsDto.FullReportsResponseDto>> getReports(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) AssetCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        User currentUser = getCurrentUser();
        ReportsDto.FullReportsResponseDto report = reportsService.getReports(
                currentUser, baseId, category, startDate, endDate
        );
        return ResponseEntity.ok(ApiResponse.success("Reports and analytics generated successfully", report));
    }
}
