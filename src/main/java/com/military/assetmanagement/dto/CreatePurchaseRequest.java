package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePurchaseRequest {

    @Size(max = 50, message = "Reference number must not exceed 50 characters")
    private String referenceNumber;

    private Long assetId;

    @NotBlank(message = "Asset name is required")
    @Size(max = 150, message = "Asset name must not exceed 150 characters")
    private String assetName;

    @NotNull(message = "Category is required")
    private AssetCategory category;

    private String equipmentType;

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String unit = "Units";
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;

    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 150, message = "Supplier name must not exceed 150 characters")
    private String supplier;

    private String notes;

    public CreatePurchaseRequest() {
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
}
