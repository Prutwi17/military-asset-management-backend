package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.Expenditure;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ExpenditureDto {

    private Long id;
    private String referenceNumber;
    private Long assetId;
    private String assetCode;
    private String assetName;
    private AssetCategory category;
    private String equipmentType;
    private BaseSummaryDto base;
    private Integer quantity;
    private String unit;
    private String personnelOrUnit;
    private LocalDate expenditureDate;
    private String reason;
    private String reference;
    private String recordedBy;
    private Long recordedByUserId;
    private String notes;
    private LocalDateTime createdAt;

    public ExpenditureDto() {
    }

    public static ExpenditureDto fromEntity(Expenditure e) {
        ExpenditureDto dto = new ExpenditureDto();
        dto.setId(e.getId());
        dto.setReferenceNumber(e.getReferenceNumber());
        if (e.getAsset() != null) {
            dto.setAssetId(e.getAsset().getId());
            dto.setAssetCode(e.getAsset().getAssetCode());
        }
        dto.setAssetName(e.getAssetName());
        dto.setCategory(e.getCategory());
        dto.setEquipmentType(e.getEquipmentType());
        dto.setBase(BaseSummaryDto.fromEntity(e.getBase()));
        dto.setQuantity(e.getQuantity());
        dto.setUnit(e.getUnit());
        dto.setPersonnelOrUnit(e.getPersonnelOrUnit());
        dto.setExpenditureDate(e.getExpenditureDate());
        dto.setReason(e.getReason());
        dto.setReference(e.getReference());
        dto.setRecordedBy(e.getRecordedBy());
        dto.setRecordedByUserId(e.getRecordedByUserId());
        dto.setNotes(e.getNotes());
        dto.setCreatedAt(e.getCreatedAt());
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

    public BaseSummaryDto getBase() {
        return base;
    }

    public void setBase(BaseSummaryDto base) {
        this.base = base;
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

    public String getPersonnelOrUnit() {
        return personnelOrUnit;
    }

    public void setPersonnelOrUnit(String personnelOrUnit) {
        this.personnelOrUnit = personnelOrUnit;
    }

    public LocalDate getExpenditureDate() {
        return expenditureDate;
    }

    public void setExpenditureDate(LocalDate expenditureDate) {
        this.expenditureDate = expenditureDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public Long getRecordedByUserId() {
        return recordedByUserId;
    }

    public void setRecordedByUserId(Long recordedByUserId) {
        this.recordedByUserId = recordedByUserId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
