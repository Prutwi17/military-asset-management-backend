package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.AssignmentDto;
import com.military.assetmanagement.dto.CreateAssignmentRequest;
import com.military.assetmanagement.dto.ReturnAssignmentRequest;
import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.AssignmentRepository;
import com.military.assetmanagement.repository.BaseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssetRepository assetRepository;
    private final BaseRepository baseRepository;
    private final AuditService auditService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             AssetRepository assetRepository,
                             BaseRepository baseRepository,
                             AuditService auditService) {
        this.assignmentRepository = assignmentRepository;
        this.assetRepository = assetRepository;
        this.baseRepository = baseRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> getAssignments(User currentUser, Long baseId, AssignmentStatus status,
                                              LocalDate startDate, LocalDate endDate, String keyword) {
        Long effectiveBaseId = baseId;

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null) {
                return List.of();
            }
            effectiveBaseId = currentUser.getBase().getId();
        }

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        List<Assignment> assignments = assignmentRepository.filterAssignments(
                effectiveBaseId, status, startDate, endDate, searchKeyword
        );

        return assignments.stream().map(AssignmentDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentDto getAssignmentById(Long id, User currentUser) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(assignment.getBase().getId())) {
                throw new AccessDeniedException("Access Denied: Commander cannot view assignments outside assigned base.");
            }
        }

        return AssignmentDto.fromEntity(assignment);
    }

    @Transactional
    public AssignmentDto createAssignment(CreateAssignmentRequest request, User currentUser) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + request.getAssetId()));

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + request.getBaseId()));

        if (!asset.getBase().getId().equals(base.getId())) {
            throw new IllegalArgumentException("Asset '" + asset.getName() + "' is stationed at base '" +
                    asset.getBase().getName() + "', not at requested base '" + base.getName() + "'.");
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(base.getId())) {
                throw new AccessDeniedException("Access Denied: Commander can only assign assets stationed at their assigned base.");
            }
        }

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Assignment quantity must be greater than zero.");
        }

        if (asset.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient inventory available to assign. Available: " +
                    asset.getQuantity() + ", requested: " + request.getQuantity());
        }

        // Allocate inventory from available pool
        asset.setQuantity(asset.getQuantity() - request.getQuantity());
        if (asset.getQuantity() == 0) {
            asset.setStatus(AssetStatus.IN_USE);
        }
        assetRepository.save(asset);

        String ref = request.getReferenceNumber();
        if (ref == null || ref.trim().isEmpty()) {
            ref = "ASN-" + LocalDate.now().getYear() + "-" + String.format("%03d", assignmentRepository.count() + 1);
        } else if (assignmentRepository.existsByReferenceNumber(ref.trim())) {
            throw new IllegalArgumentException("Assignment reference number '" + ref + "' already exists.");
        }

        Assignment assignment = new Assignment(
                ref.trim(),
                asset,
                asset.getName(),
                asset.getCategory(),
                base,
                request.getPersonnelName().trim(),
                request.getPersonnelRank(),
                request.getPersonnelId().trim(),
                request.getUnitDivision(),
                request.getQuantity(),
                asset.getUnit(),
                request.getAssignmentDate() != null ? request.getAssignmentDate() : LocalDate.now(),
                request.getExpectedReturnDate(),
                AssignmentStatus.ACTIVE,
                currentUser.getFullName() + " (" + currentUser.getUsername() + ")",
                request.getNotes()
        );

        Assignment saved = assignmentRepository.save(assignment);

        auditService.recordAudit(
                currentUser,
                "CREATE_ASSIGNMENT",
                "Assignment",
                saved.getId(),
                "Assigned " + saved.getQuantity() + "x " + asset.getName() + " to " +
                (saved.getPersonnelRank() != null ? saved.getPersonnelRank() + " " : "") + saved.getPersonnelName() +
                " (" + saved.getPersonnelId() + ") at " + base.getName() + ". Ref: " + saved.getReferenceNumber()
        );

        return AssignmentDto.fromEntity(saved);
    }

    @Transactional
    public AssignmentDto returnAssignment(Long id, ReturnAssignmentRequest request, User currentUser) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + id));

        if (assignment.getStatus() == AssignmentStatus.RETURNED) {
            throw new IllegalStateException("Assignment " + assignment.getReferenceNumber() + " has already been returned.");
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(assignment.getBase().getId())) {
                throw new AccessDeniedException("Access Denied: Commander cannot return assignments outside assigned base.");
            }
        }

        // Return allocated quantity back to available inventory
        Asset asset = assignment.getAsset();
        asset.setQuantity(asset.getQuantity() + assignment.getQuantity());
        if (asset.getStatus() == AssetStatus.IN_USE) {
            asset.setStatus(AssetStatus.AVAILABLE);
        }
        assetRepository.save(asset);

        assignment.setStatus(AssignmentStatus.RETURNED);
        assignment.setActualReturnDate(request.getReturnDate() != null ? request.getReturnDate() : LocalDate.now());
        assignment.setReturnCondition(request.getReturnCondition() != null ? request.getReturnCondition() : "GOOD");
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            assignment.setNotes(
                    (assignment.getNotes() != null ? assignment.getNotes() + " | Return Note: " : "Return Note: ") +
                    request.getNotes()
            );
        }

        Assignment saved = assignmentRepository.save(assignment);

        auditService.recordAudit(
                currentUser,
                "RETURN_ASSIGNMENT",
                "Assignment",
                saved.getId(),
                "Returned " + saved.getQuantity() + "x " + saved.getAssetName() + " from " +
                saved.getPersonnelName() + " (" + saved.getPersonnelId() + "). Condition: " + saved.getReturnCondition()
        );

        return AssignmentDto.fromEntity(saved);
    }
}
