package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import com.military.assetmanagement.dto.CreatePurchaseRequest;
import com.military.assetmanagement.dto.PurchaseDto;
import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.service.PurchaseService;
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
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final UserRepository userRepository;

    public PurchaseController(PurchaseService purchaseService, UserRepository userRepository) {
        this.purchaseService = purchaseService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PurchaseDto>>> getPurchases(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) AssetCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword
    ) {
        User user = getCurrentUser();
        List<PurchaseDto> purchases = purchaseService.getPurchases(user, baseId, category, startDate, endDate, keyword);
        return ResponseEntity.ok(ApiResponse.success("Purchases retrieved successfully", purchases));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseDto>> getPurchaseById(@PathVariable Long id) {
        User user = getCurrentUser();
        PurchaseDto purchase = purchaseService.getPurchaseById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Purchase details retrieved successfully", purchase));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<PurchaseDto>> createPurchase(@Valid @RequestBody CreatePurchaseRequest request) {
        User user = getCurrentUser();
        PurchaseDto created = purchaseService.createPurchase(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase recorded and inventory updated successfully", created));
    }
}
