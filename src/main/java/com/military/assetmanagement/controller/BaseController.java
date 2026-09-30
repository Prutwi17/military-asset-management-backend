package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import com.military.assetmanagement.dto.BaseDto;
import com.military.assetmanagement.dto.CreateBaseRequest;
import com.military.assetmanagement.dto.UpdateBaseRequest;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.service.BaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bases")
public class BaseController {

    private final BaseService baseService;
    private final UserRepository userRepository;

    public BaseController(BaseService baseService, UserRepository userRepository) {
        this.baseService = baseService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BaseDto>>> getAllBases() {
        User user = getCurrentUser();
        List<BaseDto> bases = baseService.getAllBases(user);
        return ResponseEntity.ok(ApiResponse.success("Bases retrieved successfully", bases));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BaseDto>> getBaseById(@PathVariable Long id) {
        User user = getCurrentUser();
        BaseDto base = baseService.getBaseById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Base details retrieved successfully", base));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BaseDto>> createBase(@Valid @RequestBody CreateBaseRequest request) {
        BaseDto created = baseService.createBase(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Base created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BaseDto>> updateBase(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateBaseRequest request) {
        BaseDto updated = baseService.updateBase(id, request);
        return ResponseEntity.ok(ApiResponse.success("Base updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBase(@PathVariable Long id) {
        baseService.deleteBase(id);
        return ResponseEntity.ok(ApiResponse.success("Base deactivated successfully", null));
    }
}
