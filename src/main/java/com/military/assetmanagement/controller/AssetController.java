package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.*;
import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.AssetStatus;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;
    private final UserRepository userRepository;

    public AssetController(AssetService assetService, UserRepository userRepository) {
        this.assetService = assetService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssetDto>>> getAssets(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) AssetCategory category,
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) String equipmentType,
            @RequestParam(required = false) String keyword
    ) {
        User user = getCurrentUser();
        List<AssetDto> assets = assetService.getAssets(user, baseId, category, status, equipmentType, keyword);
        return ResponseEntity.ok(ApiResponse.success("Assets retrieved successfully", assets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssetDto>> getAssetById(@PathVariable Long id) {
        User user = getCurrentUser();
        AssetDto asset = assetService.getAssetById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Asset details retrieved successfully", asset));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<AssetDto>> createAsset(@Valid @RequestBody CreateAssetRequest request) {
        User user = getCurrentUser();
        AssetDto created = assetService.createAsset(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Asset registered successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<AssetDto>> updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssetRequest request
    ) {
        User user = getCurrentUser();
        AssetDto updated = assetService.updateAsset(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("Asset updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<AssetDto>> changeAssetStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request
    ) {
        User user = getCurrentUser();
        AssetDto updated = assetService.changeAssetStatus(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("Asset status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable Long id) {
        User user = getCurrentUser();
        assetService.deleteAsset(id, user);
        return ResponseEntity.ok(ApiResponse.success("Asset deactivated successfully", null));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<AssetCategory>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved", Arrays.asList(AssetCategory.values())));
    }

    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<List<AssetStatus>>> getStatuses() {
        return ResponseEntity.ok(ApiResponse.success("Statuses retrieved", Arrays.asList(AssetStatus.values())));
    }
}
