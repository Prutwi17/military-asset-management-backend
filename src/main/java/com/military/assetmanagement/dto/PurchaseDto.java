package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PurchaseDto {
    private Long id;
    private String referenceNumber;
    private Long assetId;
    private String assetName;
    private String assetCode;
    private AssetCategory category;
    private String equipmentType;
    private Long baseId;
    private String baseName;
    private String baseCode;
    private Integer quantity;
    private String unit;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private LocalDate purchaseDate;
    private String supplier;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;

    public PurchaseDto() {
    }

    public static PurchaseDto fromEntity(Purchase purchase) {
        if (purchase == null) return null;
        PurchaseDto dto = new PurchaseDto();
        dto.setId(purchase.getId());
        dto.setReferenceNumber(purchase.getReferenceNumber());
        if (purchase.getAsset() != null) {
            dto.setAssetId(purchase.getAsset().getId());
            dto.setAssetCode(purchase.getAsset().getAssetCode());
        }
        dto.setAssetName(purchase.getAssetName());
        dto.setCategory(purchase.getCategory());
        dto.setEquipmentType(purchase.getEquipmentType());
        if (purchase.getBase() != null) {
            dto.setBaseId(purchase.getBase().getId());
            dto.setBaseName(purchase.getBase().getName());
            dto.setBaseCode(purchase.getBase().getCode());
        }
        dto.setQuantity(purchase.getQuantity());
        dto.setUnit(purchase.getUnit());
        dto.setUnitPrice(purchase.getUnitPrice());
        dto.setTotalAmount(purchase.getTotalAmount());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setSupplier(purchase.getSupplier());
        dto.setNotes(purchase.getNotes());
        dto.setCreatedBy(purchase.getCreatedBy());
        dto.setCreatedAt(purchase.getCreatedAt());
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

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
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

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
