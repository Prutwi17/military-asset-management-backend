package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import com.military.assetmanagement.dto.CreateTransferRequest;
import com.military.assetmanagement.dto.RejectTransferRequest;
import com.military.assetmanagement.dto.TransferDto;
import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.TransferStatus;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.service.TransferService;
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
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;
    private final UserRepository userRepository;

    public TransferController(TransferService transferService, UserRepository userRepository) {
        this.transferService = transferService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransferDto>>> getTransfers(
            @RequestParam(required = false) Long sourceBaseId,
            @RequestParam(required = false) Long destinationBaseId,
            @RequestParam(required = false) AssetCategory category,
            @RequestParam(required = false) TransferStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword
    ) {
        User user = getCurrentUser();
        List<TransferDto> transfers = transferService.getTransfers(
                user, sourceBaseId, destinationBaseId, category, status, startDate, endDate, keyword
        );
        return ResponseEntity.ok(ApiResponse.success("Transfers retrieved successfully", transfers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransferDto>> getTransferById(@PathVariable Long id) {
        User user = getCurrentUser();
        TransferDto transfer = transferService.getTransferById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Transfer retrieved successfully", transfer));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<TransferDto>> createTransfer(@Valid @RequestBody CreateTransferRequest request) {
        User user = getCurrentUser();
        TransferDto created = transferService.createTransfer(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer initiated successfully", created));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<TransferDto>> approveTransfer(@PathVariable Long id) {
        User user = getCurrentUser();
        TransferDto approved = transferService.approveTransfer(id, user);
        return ResponseEntity.ok(ApiResponse.success("Transfer approved successfully", approved));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    public ResponseEntity<ApiResponse<TransferDto>> rejectTransfer(
            @PathVariable Long id,
            @Valid @RequestBody RejectTransferRequest request
    ) {
        User user = getCurrentUser();
        TransferDto rejected = transferService.rejectTransfer(id, request, user);
        return ResponseEntity.ok(ApiResponse.success("Transfer rejected", rejected));
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<TransferDto>> completeTransfer(@PathVariable Long id) {
        User user = getCurrentUser();
        TransferDto completed = transferService.completeTransfer(id, user);
        return ResponseEntity.ok(ApiResponse.success("Transfer completed and inventory updated", completed));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<TransferDto>> cancelTransfer(@PathVariable Long id) {
        User user = getCurrentUser();
        TransferDto cancelled = transferService.cancelTransfer(id, user);
        return ResponseEntity.ok(ApiResponse.success("Transfer cancelled successfully", cancelled));
    }
}
