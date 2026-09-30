package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.CreateExpenditureRequest;
import com.military.assetmanagement.dto.ExpenditureDto;
import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.BaseRepository;
import com.military.assetmanagement.repository.ExpenditureRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    private final AssetRepository assetRepository;
    private final BaseRepository baseRepository;
    private final AuditService auditService;

    public ExpenditureService(ExpenditureRepository expenditureRepository,
                              AssetRepository assetRepository,
                              BaseRepository baseRepository,
                              AuditService auditService) {
        this.expenditureRepository = expenditureRepository;
        this.assetRepository = assetRepository;
        this.baseRepository = baseRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<ExpenditureDto> getExpenditures(User currentUser, Long baseId, AssetCategory category,
                                                LocalDate startDate, LocalDate endDate, String keyword) {
        Long effectiveBaseId = baseId;

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null) {
                return List.of();
            }
            effectiveBaseId = currentUser.getBase().getId();
        }

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        List<Expenditure> list = expenditureRepository.filterExpenditures(
                effectiveBaseId, category, startDate, endDate, searchKeyword
        );

        return list.stream().map(ExpenditureDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenditureDto getExpenditureById(Long id, User currentUser) {
        Expenditure expenditure = expenditureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expenditure not found with id: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(expenditure.getBase().getId())) {
                throw new AccessDeniedException("Access Denied: Commander cannot view expenditures outside assigned base.");
            }
        }

        return ExpenditureDto.fromEntity(expenditure);
    }

    @Transactional
    public ExpenditureDto recordExpenditure(CreateExpenditureRequest request, User currentUser) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + request.getAssetId()));

        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + request.getBaseId()));

        if (!asset.getBase().getId().equals(base.getId())) {
            throw new IllegalArgumentException("Asset '" + asset.getName() + "' is stationed at base '" +
                    asset.getBase().getName() + "', not at '" + base.getName() + "'.");
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(base.getId())) {
                throw new AccessDeniedException("Access Denied: Commander can only log expenditures for assigned base.");
            }
        }

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Expenditure quantity must be greater than zero.");
        }

        // CRITICAL REQUIREMENT: Prevent expenditure quantities greater than available inventory!
        if (asset.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Expenditure rejected: Requested quantity (" + request.getQuantity() +
                    ") exceeds available inventory (" + asset.getQuantity() + ") for asset '" + asset.getName() + "'."
            );
        }

        // CRITICAL REQUIREMENT: Available inventory must decrease appropriately
        int remainingQuantity = asset.getQuantity() - request.getQuantity();
        asset.setQuantity(remainingQuantity);
        if (remainingQuantity == 0) {
            // When fully expended, update status to reflect depletion
            asset.setStatus(AssetStatus.DECOMMISSIONED);
        }
        assetRepository.save(asset);

        String ref = request.getReferenceNumber();
        if (ref == null || ref.trim().isEmpty()) {
            ref = "EXP-" + LocalDate.now().getYear() + "-" + String.format("%03d", expenditureRepository.count() + 1);
        } else if (expenditureRepository.existsByReferenceNumber(ref.trim())) {
            throw new IllegalArgumentException("Expenditure reference number '" + ref + "' already exists.");
        }

        Expenditure expenditure = new Expenditure(
                ref.trim(),
                asset,
                asset.getName(),
                asset.getCategory(),
                asset.getEquipmentType(),
                base,
                request.getQuantity(),
                request.getUnit() != null ? request.getUnit() : asset.getUnit(),
                request.getPersonnelOrUnit().trim(),
                request.getExpenditureDate() != null ? request.getExpenditureDate() : LocalDate.now(),
                request.getReason().trim(),
                request.getReference(),
                currentUser.getFullName() + " (" + currentUser.getUsername() + ")",
                currentUser.getId(),
                request.getNotes()
        );

        Expenditure saved = expenditureRepository.save(expenditure);

        // Record Audit
        auditService.recordAudit(
                currentUser,
                "RECORD_EXPENDITURE",
                "Expenditure",
                saved.getId(),
                "Expended " + saved.getQuantity() + "x " + asset.getName() + " by " + saved.getPersonnelOrUnit() +
                " at " + base.getName() + ". Reason: " + saved.getReason() + ". Remaining inventory: " + remainingQuantity
        );

        return ExpenditureDto.fromEntity(saved);
    }
}
