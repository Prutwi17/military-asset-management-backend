package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.AssetStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssetDto {
    private Long id;
    private String assetCode;
    private String name;
    private AssetCategory category;
    private String equipmentType;
    private String serialNumber;
    private Integer quantity;
    private String unit;
    private AssetStatus status;
    private Long baseId;
    private String baseName;
    private String baseCode;
    private String location;
    private String description;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private LocalDate lastMaintenanceDate;
    private String imageUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssetDto() {
    }

    public static AssetDto fromEntity(Asset asset) {
        if (asset == null) return null;
        AssetDto dto = new AssetDto();
        dto.setId(asset.getId());
        dto.setAssetCode(asset.getAssetCode());
        dto.setName(asset.getName());
        dto.setCategory(asset.getCategory());
        dto.setEquipmentType(asset.getEquipmentType());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setQuantity(asset.getQuantity());
        dto.setUnit(asset.getUnit());
        dto.setStatus(asset.getStatus());
        if (asset.getBase() != null) {
            dto.setBaseId(asset.getBase().getId());
            dto.setBaseName(asset.getBase().getName());
            dto.setBaseCode(asset.getBase().getCode());
        }
        dto.setLocation(asset.getLocation());
        dto.setDescription(asset.getDescription());
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setPurchasePrice(asset.getPurchasePrice());
        dto.setLastMaintenanceDate(asset.getLastMaintenanceDate());
        dto.setImageUrl(asset.getImageUrl());
        dto.setActive(asset.isActive());
        dto.setCreatedAt(asset.getCreatedAt());
        dto.setUpdatedAt(asset.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
}
