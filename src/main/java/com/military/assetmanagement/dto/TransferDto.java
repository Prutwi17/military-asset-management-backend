package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.Transfer;
import com.military.assetmanagement.entity.TransferStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransferDto {

    private Long id;
    private String referenceNumber;
    private Long assetId;
    private String assetCode;
    private String assetName;
    private AssetCategory category;
    private String equipmentType;
    private Integer quantity;
    private String unit;
    private BaseSummaryDto sourceBase;
    private BaseSummaryDto destinationBase;
    private LocalDate transferDate;
    private TransferStatus status;
    private String requestedBy;
    private Long requestedByUserId;
    private String approvedBy;
    private Long approvedByUserId;
    private String reason;
    private String notes;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public TransferDto() {
    }

    public static TransferDto fromEntity(Transfer t) {
        TransferDto dto = new TransferDto();
        dto.setId(t.getId());
        dto.setReferenceNumber(t.getReferenceNumber());
        if (t.getAsset() != null) {
            dto.setAssetId(t.getAsset().getId());
            dto.setAssetCode(t.getAsset().getAssetCode());
        }
        dto.setAssetName(t.getAssetName());
        dto.setCategory(t.getCategory());
        dto.setEquipmentType(t.getEquipmentType());
        dto.setQuantity(t.getQuantity());
        dto.setUnit(t.getUnit());
        dto.setSourceBase(BaseSummaryDto.fromEntity(t.getSourceBase()));
        dto.setDestinationBase(BaseSummaryDto.fromEntity(t.getDestinationBase()));
        dto.setTransferDate(t.getTransferDate());
        dto.setStatus(t.getStatus());
        dto.setRequestedBy(t.getRequestedBy());
        dto.setRequestedByUserId(t.getRequestedByUserId());
        dto.setApprovedBy(t.getApprovedBy());
        dto.setApprovedByUserId(t.getApprovedByUserId());
        dto.setReason(t.getReason());
        dto.setNotes(t.getNotes());
        dto.setRejectionReason(t.getRejectionReason());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setUpdatedAt(t.getUpdatedAt());
        dto.setCompletedAt(t.getCompletedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BaseSummaryDto getSourceBase() {
        return sourceBase;
    }

    public void setSourceBase(BaseSummaryDto sourceBase) {
        this.sourceBase = sourceBase;
    }

    public BaseSummaryDto getDestinationBase() {
        return destinationBase;
    }

    public void setDestinationBase(BaseSummaryDto destinationBase) {
        this.destinationBase = destinationBase;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Long getRequestedByUserId() {
        return requestedByUserId;
    }

    public void setRequestedByUserId(Long requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Long getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(Long approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
