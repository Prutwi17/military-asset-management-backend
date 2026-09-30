package com.military.assetmanagement.controller;

import com.military.assetmanagement.dto.ApiResponse;
import com.military.assetmanagement.dto.AssignmentDto;
import com.military.assetmanagement.dto.CreateAssignmentRequest;
import com.military.assetmanagement.dto.ReturnAssignmentRequest;
import com.military.assetmanagement.entity.AssignmentStatus;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.UserRepository;
import com.military.assetmanagement.service.AssignmentService;
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
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserRepository userRepository;

    public AssignmentController(AssignmentService assignmentService, UserRepository userRepository) {
        this.assignmentService = assignmentService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssignmentDto>>> getAssignments(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword
    ) {
        User user = getCurrentUser();
        List<AssignmentDto> assignments = assignmentService.getAssignments(user, baseId, status, startDate, endDate, keyword);
        return ResponseEntity.ok(ApiResponse.success("Assignments retrieved successfully", assignments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentDto>> getAssignmentById(@PathVariable Long id) {
        User user = getCurrentUser();
        AssignmentDto assignment = assignmentService.getAssignmentById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Assignment retrieved successfully", assignment));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<AssignmentDto>> createAssignment(@Valid @RequestBody CreateAssignmentRequest request) {
        User user = getCurrentUser();
        AssignmentDto created = assignmentService.createAssignment(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Asset assigned to personnel successfully", created));
    }

    @PutMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER', 'LOGISTICS_OFFICER')")
    public ResponseEntity<ApiResponse<AssignmentDto>> returnAssignment(
            @PathVariable Long id,
            @RequestBody(required = false) ReturnAssignmentRequest request
    ) {
        User user = getCurrentUser();
        ReturnAssignmentRequest req = request != null ? request : new ReturnAssignmentRequest();
        AssignmentDto returned = assignmentService.returnAssignment(id, req, user);
        return ResponseEntity.ok(ApiResponse.success("Asset returned into base inventory", returned));
    }
}
