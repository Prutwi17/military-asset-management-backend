package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.CreateTransferRequest;
import com.military.assetmanagement.dto.RejectTransferRequest;
import com.military.assetmanagement.dto.TransferDto;
import com.military.assetmanagement.entity.*;
import com.military.assetmanagement.exception.ResourceNotFoundException;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.BaseRepository;
import com.military.assetmanagement.repository.TransferRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AssetRepository assetRepository;
    private final BaseRepository baseRepository;
    private final AuditService auditService;

    public TransferService(TransferRepository transferRepository,
                           AssetRepository assetRepository,
                           BaseRepository baseRepository,
                           AuditService auditService) {
        this.transferRepository = transferRepository;
        this.assetRepository = assetRepository;
        this.baseRepository = baseRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<TransferDto> getTransfers(User currentUser, Long sourceBaseId, Long destinationBaseId,
                                          AssetCategory category, TransferStatus status,
                                          LocalDate startDate, LocalDate endDate, String keyword) {
        Long eitherBaseId = null;
        Long effectiveSourceId = sourceBaseId;
        Long effectiveDestId = destinationBaseId;

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null) {
                return List.of();
            }
            // Base Commander sees all transfers where their base is either source or destination
            eitherBaseId = currentUser.getBase().getId();
        }

        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;

        List<Transfer> transfers = transferRepository.filterTransfers(
                effectiveSourceId, effectiveDestId, eitherBaseId, category, status, startDate, endDate, searchKeyword
        );

        return transfers.stream().map(TransferDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransferDto getTransferById(Long id, User currentUser) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + id));

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            Long cmdBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : -1L;
            if (!transfer.getSourceBase().getId().equals(cmdBaseId) &&
                !transfer.getDestinationBase().getId().equals(cmdBaseId)) {
                throw new AccessDeniedException("Access Denied: Base Commander cannot view transfers unrelated to assigned base.");
            }
        }

        return TransferDto.fromEntity(transfer);
    }

    @Transactional
    public TransferDto createTransfer(CreateTransferRequest request, User currentUser) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with id: " + request.getAssetId()));

        Base sourceBase = asset.getBase();
        Base destinationBase = baseRepository.findById(request.getDestinationBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination base not found with id: " + request.getDestinationBaseId()));

        if (sourceBase.getId().equals(destinationBase.getId())) {
            throw new IllegalArgumentException("Source base and destination base cannot be the same.");
        }

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be greater than zero.");
        }

        if (asset.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient inventory at source base '" + sourceBase.getName() + 
                    "'. Available quantity: " + asset.getQuantity() + ", requested: " + request.getQuantity()
            );
        }

        // RBAC validation
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            if (currentUser.getBase() == null || !currentUser.getBase().getId().equals(sourceBase.getId())) {
                throw new AccessDeniedException("Access Denied: Base Commander can only initiate transfers from their assigned base.");
            }
        }

        // Generate reference number
        String ref = request.getReferenceNumber();
        if (ref == null || ref.trim().isEmpty()) {
            ref = "TRF-" + LocalDate.now().getYear() + "-" + String.format("%03d", transferRepository.count() + 1);
        } else if (transferRepository.existsByReferenceNumber(ref.trim())) {
            throw new IllegalArgumentException("Transfer reference number '" + ref + "' already exists.");
        }

        Transfer transfer = new Transfer(
                ref.trim(),
                asset,
                asset.getName(),
                asset.getCategory(),
                asset.getEquipmentType(),
                request.getQuantity(),
                asset.getUnit(),
                sourceBase,
                destinationBase,
                request.getTransferDate() != null ? request.getTransferDate() : LocalDate.now(),
                TransferStatus.PENDING,
                currentUser.getFullName() + " (" + currentUser.getUsername() + ")",
                currentUser.getId(),
                request.getReason().trim(),
                request.getNotes()
        );

        Transfer saved = transferRepository.save(transfer);

        auditService.recordAudit(
                currentUser,
                "CREATE_TRANSFER",
                "Transfer",
                saved.getId(),
                "Initiated transfer " + ref + " of " + request.getQuantity() + "x " + asset.getName() +
                " from " + sourceBase.getName() + " to " + destinationBase.getName() + ". Reason: " + request.getReason()
        );

        return TransferDto.fromEntity(saved);
    }

    @Transactional
    public TransferDto approveTransfer(Long id, User currentUser) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + id));

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING transfers can be approved. Current status: " + transfer.getStatus());
        }

        // RBAC validation: Admin, Destination Base Commander, or Source Base Commander
        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            Long cmdBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : -1L;
            if (!transfer.getSourceBase().getId().equals(cmdBaseId) &&
                !transfer.getDestinationBase().getId().equals(cmdBaseId)) {
                throw new AccessDeniedException("Access Denied: Commander cannot approve transfers outside assigned command.");
            }
        }

        transfer.setStatus(TransferStatus.APPROVED);
        transfer.setApprovedBy(currentUser.getFullName() + " (" + currentUser.getUsername() + ")");
        transfer.setApprovedByUserId(currentUser.getId());

        Transfer saved = transferRepository.save(transfer);

        auditService.recordAudit(
                currentUser,
                "APPROVE_TRANSFER",
                "Transfer",
                saved.getId(),
                "Approved transfer " + saved.getReferenceNumber() + " (" + saved.getQuantity() + "x " + saved.getAssetName() + ")"
        );

        return TransferDto.fromEntity(saved);
    }

    @Transactional
    public TransferDto rejectTransfer(Long id, RejectTransferRequest request, User currentUser) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + id));

        if (transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING transfers can be rejected. Current status: " + transfer.getStatus());
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            Long cmdBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : -1L;
            if (!transfer.getSourceBase().getId().equals(cmdBaseId) &&
                !transfer.getDestinationBase().getId().equals(cmdBaseId)) {
                throw new AccessDeniedException("Access Denied: Commander cannot reject transfers outside assigned command.");
            }
        }

        transfer.setStatus(TransferStatus.REJECTED);
        transfer.setRejectionReason(request.getReason());
        transfer.setApprovedBy(currentUser.getFullName() + " (" + currentUser.getUsername() + ")");
        transfer.setApprovedByUserId(currentUser.getId());

        Transfer saved = transferRepository.save(transfer);

        auditService.recordAudit(
                currentUser,
                "REJECT_TRANSFER",
                "Transfer",
                saved.getId(),
                "Rejected transfer " + saved.getReferenceNumber() + ". Reason: " + request.getReason()
        );

        return TransferDto.fromEntity(saved);
    }

    @Transactional
    public TransferDto completeTransfer(Long id, User currentUser) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + id));

        if (transfer.getStatus() != TransferStatus.APPROVED && transfer.getStatus() != TransferStatus.PENDING) {
            throw new IllegalStateException("Transfer must be in APPROVED or PENDING status to complete. Current status: " + transfer.getStatus());
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            Long cmdBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : -1L;
            // Destination commander acknowledges arrival and completes transfer into base inventory
            if (!transfer.getDestinationBase().getId().equals(cmdBaseId) &&
                !transfer.getSourceBase().getId().equals(cmdBaseId)) {
                throw new AccessDeniedException("Access Denied: Commander cannot complete transfers for other bases.");
            }
        }

        Asset sourceAsset = transfer.getAsset();
        if (sourceAsset.getQuantity() < transfer.getQuantity()) {
            throw new IllegalArgumentException(
                    "Transfer failed: Source base inventory depleted below required amount. Available: " +
                    sourceAsset.getQuantity() + ", Required: " + transfer.getQuantity()
            );
        }

        // 1. Decrement source base inventory
        sourceAsset.setQuantity(sourceAsset.getQuantity() - transfer.getQuantity());
        assetRepository.save(sourceAsset);

        // 2. Increment destination base inventory
        Base destBase = transfer.getDestinationBase();
        List<Asset> existingDestAssets = assetRepository.filterAssets(
                destBase.getId(),
                transfer.getCategory(),
                null,
                null,
                transfer.getAssetName()
        );

        Asset destAsset;
        if (!existingDestAssets.isEmpty()) {
            destAsset = existingDestAssets.get(0);
            destAsset.setQuantity(destAsset.getQuantity() + transfer.getQuantity());
            assetRepository.save(destAsset);
        } else {
            // Create new asset entry at destination base
            String newAssetCode = "AST-" + String.format("%03d", assetRepository.count() + 1);
            destAsset = new Asset(
                    newAssetCode,
                    transfer.getAssetName(),
                    transfer.getCategory(),
                    transfer.getEquipmentType() != null ? transfer.getEquipmentType() : "Transferred Military Asset",
                    "SN-" + System.currentTimeMillis() % 1000000,
                    transfer.getQuantity(),
                    transfer.getUnit() != null ? transfer.getUnit() : "Units",
                    AssetStatus.AVAILABLE,
                    destBase,
                    destBase.getName() + " - Armory / Depot",
                    "Received via transfer " + transfer.getReferenceNumber() + " from " + transfer.getSourceBase().getName(),
                    transfer.getTransferDate(),
                    sourceAsset.getPurchasePrice(),
                    null,
                    sourceAsset.getImageUrl()
            );
            assetRepository.save(destAsset);
        }

        // 3. Mark transfer as COMPLETED
        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCompletedAt(LocalDateTime.now());
        if (transfer.getApprovedBy() == null) {
            transfer.setApprovedBy(currentUser.getFullName() + " (" + currentUser.getUsername() + ")");
            transfer.setApprovedByUserId(currentUser.getId());
        }

        Transfer saved = transferRepository.save(transfer);

        auditService.recordAudit(
                currentUser,
                "COMPLETE_TRANSFER",
                "Transfer",
                saved.getId(),
                "Completed transfer " + saved.getReferenceNumber() + ": Shifted " + saved.getQuantity() + "x " +
                saved.getAssetName() + " from " + saved.getSourceBase().getName() + " to " + saved.getDestinationBase().getName() +
                ". Source remaining: " + sourceAsset.getQuantity() + ", Destination total: " + destAsset.getQuantity()
        );

        return TransferDto.fromEntity(saved);
    }

    @Transactional
    public TransferDto cancelTransfer(Long id, User currentUser) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with id: " + id));

        if (transfer.getStatus() == TransferStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed transfer. Inventory has already moved.");
        }
        if (transfer.getStatus() == TransferStatus.CANCELLED) {
            throw new IllegalStateException("Transfer is already cancelled.");
        }

        if (currentUser.getRole() == Role.BASE_COMMANDER) {
            Long cmdBaseId = currentUser.getBase() != null ? currentUser.getBase().getId() : -1L;
            if (!transfer.getSourceBase().getId().equals(cmdBaseId)) {
                throw new AccessDeniedException("Access Denied: Commander can only cancel transfers originating from their assigned base.");
            }
        }

        transfer.setStatus(TransferStatus.CANCELLED);
        Transfer saved = transferRepository.save(transfer);

        auditService.recordAudit(
                currentUser,
                "CANCEL_TRANSFER",
                "Transfer",
                saved.getId(),
                "Cancelled transfer " + saved.getReferenceNumber() + " (" + saved.getAssetName() + ")"
        );

        return TransferDto.fromEntity(saved);
    }
}
