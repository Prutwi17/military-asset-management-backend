package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import com.military.assetmanagement.dto.CreateExpenditureRequest;
import com.military.assetmanagement.dto.ExpenditureDto;
import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.service.ExpenditureService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenditures")
public class ExpenditureController {

    private final ExpenditureService expenditureService;
    private final UserRepository userRepository;

    public ExpenditureController(ExpenditureService expenditureService, UserRepository userRepository) {
        this.expenditureService = expenditureService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenditureDto>>> getExpenditures(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) AssetCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword
    ) {
        User user = getCurrentUser();
        List<ExpenditureDto> list = expenditureService.getExpenditures(user, baseId, category, startDate, endDate, keyword);
        return ResponseEntity.ok(ApiResponse.success("Expenditures retrieved successfully", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenditureDto>> getExpenditureById(@PathVariable Long id) {
        User user = getCurrentUser();
        ExpenditureDto dto = expenditureService.getExpenditureById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Expenditure details retrieved successfully", dto));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<ExpenditureDto>> recordExpenditure(@Valid @RequestBody CreateExpenditureRequest request) {
        User user = getCurrentUser();
        ExpenditureDto recorded = expenditureService.recordExpenditure(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expenditure recorded and inventory deducted", recorded));
    }
}
