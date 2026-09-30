package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetCategory;

import java.time.LocalDate;

public class NetMovementItemDto {
    private Long id;
    private LocalDate date;
    private String assetName;
    private String assetCode;
    private AssetCategory category;
    private int quantity; // Signed: positive for Purchase / Transfer In, negative for Transfer Out
    private String unit;
    private Long baseId;
    private String baseName;
    private String partnerBaseName; // Source base (for Transfer In) or Destination base (for Transfer Out)
    private String reference;
    private String transactionType; // PURCHASE, TRANSFER_IN, TRANSFER_OUT
    private String details;

    public NetMovementItemDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getPartnerBaseName() {
        return partnerBaseName;
    }

    public void setPartnerBaseName(String partnerBaseName) {
        this.partnerBaseName = partnerBaseName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
