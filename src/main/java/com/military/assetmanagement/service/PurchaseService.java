package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.CreatePurchaseRequest;
import com.military.assetmanagement.dto.PurchaseDto;
import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.BaseRepository;
import com.military.assetmanagement.repository.PurchaseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final AssetRepository assetRepository;
    private final BaseRepository baseRepository;
    private final AuditService auditService;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           AssetRepository assetRepository,
                           BaseRepository baseRepository,
                           AuditService auditService) {
        this.purchaseRepository = purchaseRepository;
        this.assetRepository = assetRepository;
        this.baseRepository = baseRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<PurchaseDto> getPurchases(User currentUser, Long baseId, AssetCategory category,
                                         LocalDate startDate, LocalDate endDate, String keyword) {
        Long effectiveBaseId = baseId;

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null) {
                return List.of();
            }
            effectiveBaseId = currentUser.getBase().getId();
        }

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        List<Purchase> purchases = purchaseRepository.filterPurchases(
                effectiveBaseId, category, startDate, endDate, searchKeyword
        );

        return purchases.stream().map(PurchaseDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PurchaseDto getPurchaseById(Long id, User currentUser) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(purchase.getBase().getId())) {
                throw new AccessDeniedException("Access Denied: Base Commander cannot view purchases for another base.");
            }
        }

        return PurchaseDto.fromEntity(purchase);
    }

    @Transactional
    public PurchaseDto createPurchase(CreatePurchaseRequest request, User currentUser) {
        Base base = baseRepository.findById(request.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with id: " + request.getBaseId()));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(base.getId())) {
                throw new AccessDeniedException("Access Denied: Base Commander can only log purchases for assigned base '" 
                        + (currentUser.getBase() != null ? currentUser.getBase().getName() : "None") + "'.");
            }
        }

        // Generate PO reference if not specified
        String ref = request.getReferenceNumber();
        if (ref == null || ref.trim().isEmpty()) {
            ref = "PO-" + LocalDate.now().getYear() + "-" + String.format("%03d", purchaseRepository.count() + 1);
        } else if (purchaseRepository.existsByReferenceNumber(ref.trim())) {
            throw new IllegalArgumentException("Purchase with reference number '" + ref + "' already exists.");
        }

        // Calculate total amount if needed
        BigDecimal total = request.getTotalAmount();
        if (total == null && request.getUnitPrice() != null && request.getQuantity() != null) {
            total = request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        }

        Asset linkedAsset = null;

        // INVENTORY UPDATE: A purchase must increase the appropriate inventory balance!
        if (request.getAssetId() != null) {
            linkedAsset = assetRepository.findById(request.getAssetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + request.getAssetId()));

            if (!linkedAsset.getBase().getId().equals(base.getId())) {
                throw new IllegalArgumentException("The specified asset belongs to base '" +
                        linkedAsset.getBase().getName() + "', but the purchase is for base '" + base.getName() + "'.");
            }

            // Increase existing asset quantity
            linkedAsset.setQuantity(linkedAsset.getQuantity() + request.getQuantity());
            linkedAsset.setPurchasePrice(request.getUnitPrice());
            linkedAsset.setPurchaseDate(request.getPurchaseDate());
            assetRepository.save(linkedAsset);
        } else {
            // Check if matching asset already exists at this base
            List<Asset> existingAssets = assetRepository.filterAssets(
                    base.getId(), request.getCategory(), null, null, request.getAssetName()
            );

            if (!existingAssets.isEmpty()) {
                linkedAsset = existingAssets.get(0);
                linkedAsset.setQuantity(linkedAsset.getQuantity() + request.getQuantity());
                assetRepository.save(linkedAsset);
            } else {
                // Create a new Asset entry in inventory with the purchased quantity
                String newAssetCode = "AST-" + String.format("%03d", assetRepository.count() + 1);
                linkedAsset = new Asset(
                        newAssetCode,
                        request.getAssetName(),
                        request.getCategory(),
                        request.getEquipmentType() != null ? request.getEquipmentType() : "Standard Military Issue",
                        "SN-" + System.currentTimeMillis() % 1000000,
                        request.getQuantity(),
                        request.getUnit() != null ? request.getUnit() : "Units",
                        AssetStatus.AVAILABLE,
                        base,
                        base.getName() + " - Supply Depot",
                        "Acquired via purchase order " + ref + ". Supplier: " + request.getSupplier(),
                        request.getPurchaseDate(),
                        request.getUnitPrice(),
                        null,
                        null
                );
                linkedAsset = assetRepository.save(linkedAsset);
            }
        }

        Purchase purchase = new Purchase(
                ref.trim(),
                linkedAsset,
                request.getAssetName().trim(),
                request.getCategory(),
                request.getEquipmentType(),
                base,
                request.getQuantity(),
                request.getUnit() != null ? request.getUnit() : "Units",
                request.getUnitPrice(),
                total,
                request.getPurchaseDate(),
                request.getSupplier().trim(),
                request.getNotes(),
                currentUser.getFullName() + " (" + currentUser.getUsername() + ")"
        );

        Purchase saved = purchaseRepository.save(purchase);
        auditService.recordAudit(
                currentUser,
                "CREATE_PURCHASE",
                "Purchase",
                saved.getId(),
                "Logged PO " + saved.getReferenceNumber() + " for " + saved.getQuantity() + "x " +
                saved.getAssetName() + " at " + base.getName() + ". Supplier: " + saved.getSupplier()
        );
        return PurchaseDto.fromEntity(saved);
    }
}
