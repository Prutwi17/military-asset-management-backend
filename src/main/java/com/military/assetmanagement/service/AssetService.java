package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.AssetDto;
import com.military.assetmanagement.dto.ChangeStatusRequest;
import com.military.assetmanagement.dto.CreateAssetRequest;
import com.military.assetmanagement.dto.UpdateAssetRequest;
import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.BaseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final BaseRepository baseRepository;
    private final AuditService auditService;

    public AssetService(AssetRepository assetRepository, BaseRepository baseRepository, AuditService auditService) {
        this.assetRepository = assetRepository;
        this.baseRepository = baseRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<AssetDto> getAssets(User currentUser, Long baseId, AssetCategory category,
                                   AssetStatus status, String equipmentType, String keyword) {
        Long effectiveBaseId = baseId;

        // Base Commander is strictly restricted to their assigned base
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null) {
                return List.of();
            }
            effectiveBaseId = currentUser.getBase().getId();
        }

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String eqType = (equipmentType != null && !equipmentType.trim().isEmpty()) ? equipmentType.trim() : null;

        List<Asset> assets = assetRepository.filterAssets(effectiveBaseId, category, status, eqType, searchKeyword);
        return assets.stream().map(AssetDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssetDto getAssetById(Long id, User currentUser) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));

        validateBaseAccess(asset.getBase(), currentUser, "view this asset");
        return AssetDto.fromEntity(asset);
    }

    @Transactional
    public AssetDto createAsset(CreateAssetRequest request, User currentUser) {
        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + request.getBaseId()));

        validateBaseAccess(base, currentUser, "register asset under this base");

        // Generate assetCode if not provided
        String code = request.getAssetCode();
        if (code == null || code.trim().isEmpty()) {
            code = "AST-" + String.format("%03d", assetRepository.count() + 1);
        } else if (assetRepository.existsByAssetCode(code.trim())) {
            throw new IllegalArgumentException("Asset with code '" + code + "' already exists.");
        }

        // Validate serial number if provided
        if (request.getSerialNumber() != null && !request.getSerialNumber().trim().isEmpty()) {
            if (assetRepository.existsBySerialNumber(request.getSerialNumber().trim())) {
                throw new IllegalArgumentException("Asset with serial number '" + request.getSerialNumber() + "' already exists.");
            }
        }

        Asset asset = new Asset(
                code.trim(),
                request.getName().trim(),
                request.getCategory(),
                request.getEquipmentType(),
                request.getSerialNumber(),
                request.getQuantity(),
                request.getUnit() != null ? request.getUnit() : "Units",
                request.getStatus() != null ? request.getStatus() : AssetStatus.AVAILABLE,
                base,
                request.getLocation(),
                request.getDescription(),
                request.getPurchaseDate(),
                request.getPurchasePrice(),
                request.getLastMaintenanceDate(),
                request.getImageUrl()
        );

        Asset saved = assetRepository.save(asset);
        auditService.recordAudit(
                currentUser,
                "CREATE_ASSET",
                "Asset",
                saved.getId(),
                "Registered new asset " + saved.getAssetCode() + " (" + saved.getName() + ", " +
                saved.getQuantity() + " " + saved.getUnit() + ") at " + base.getName()
        );
        return AssetDto.fromEntity(saved);
    }

    @Transactional
    public AssetDto updateAsset(Long id, UpdateAssetRequest request, User currentUser) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));

        validateBaseAccess(asset.getBase(), currentUser, "update this asset");

        if (request.getName() != null && !request.getName().isBlank()) {
            asset.setName(request.getName().trim());
        }
        if (request.getCategory() != null) {
            asset.setCategory(request.getCategory());
        }
        if (request.getEquipmentType() != null) {
            asset.setEquipmentType(request.getEquipmentType());
        }
        if (request.getSerialNumber() != null) {
            asset.setSerialNumber(request.getSerialNumber());
        }
        if (request.getQuantity() != null) {
            asset.setQuantity(request.getQuantity());
        }
        if (request.getUnit() != null) {
            asset.setUnit(request.getUnit());
        }
        if (request.getStatus() != null) {
            asset.setStatus(request.getStatus());
        }
        if (request.getLocation() != null) {
            asset.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            asset.setDescription(request.getDescription());
        }
        if (request.getPurchaseDate() != null) {
            asset.setPurchaseDate(request.getPurchaseDate());
        }
        if (request.getPurchasePrice() != null) {
            asset.setPurchasePrice(request.getPurchasePrice());
        }
        if (request.getLastMaintenanceDate() != null) {
            asset.setLastMaintenanceDate(request.getLastMaintenanceDate());
        }
        if (request.getImageUrl() != null) {
            asset.setImageUrl(request.getImageUrl());
        }

        // If base change requested (Admin only)
        if (request.getBaseId() != null && !request.getBaseId().equals(asset.getBase().getId())) {
            if (currentUser.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("Only Admin can transfer an asset's assigned base directly.");
            }
            Base newBase = baseRepository.findById(request.getBaseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target base not found with id: " + request.getBaseId()));
            asset.setBase(newBase);
        }

        Asset updated = assetRepository.save(asset);
        auditService.recordAudit(
                currentUser,
                "UPDATE_ASSET",
                "Asset",
                updated.getId(),
                "Updated asset specifications for " + updated.getAssetCode() + " (" + updated.getName() + ")"
        );
        return AssetDto.fromEntity(updated);
    }

    @Transactional
    public AssetDto changeAssetStatus(Long id, ChangeStatusRequest request, User currentUser) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));

        validateBaseAccess(asset.getBase(), currentUser, "change status of this asset");

        AssetStatus oldStatus = asset.getStatus();
        asset.setStatus(request.getStatus());
        Asset updated = assetRepository.save(asset);

        auditService.recordAudit(
                currentUser,
                "CHANGE_ASSET_STATUS",
                "Asset",
                updated.getId(),
                "Changed status of " + updated.getAssetCode() + " from " + oldStatus + " to " + request.getStatus()
        );
        return AssetDto.fromEntity(updated);
    }

    @Transactional
    public void deleteAsset(Long id, User currentUser) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + id));

        validateBaseAccess(asset.getBase(), currentUser, "delete this asset");

        // Soft delete / mark inactive
        asset.setActive(false);
        assetRepository.save(asset);

        auditService.recordAudit(
                currentUser,
                "DEACTIVATE_ASSET",
                "Asset",
                asset.getId(),
                "Deactivated asset " + asset.getAssetCode() + " (" + asset.getName() + ")"
        );
    }

    private void validateBaseAccess(Base base, User currentUser, String action) {
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(base.getId())) {
                throw new AccessDeniedException("Access Denied: Base Commander cannot " + action + " outside of assigned base '" 
                        + (currentUser.getBase() != null ? currentUser.getBase().getName() : "None") + "'.");
            }
        }
    }
}
