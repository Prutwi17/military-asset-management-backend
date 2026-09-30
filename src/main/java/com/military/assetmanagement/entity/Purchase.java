package com.military.assetmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @Column(name = "asset_name", nullable = false, length = 150)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AssetCategory category;

    @Column(name = "equipment_type", length = 100)
    private String equipmentType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 30)
    private String unit = "Units";

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(nullable = false, length = 150)
    private String supplier;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Purchase() {
    }

    public Purchase(String referenceNumber, Asset asset, String assetName, AssetCategory category,
                    String equipmentType, Base base, Integer quantity, String unit,
                    BigDecimal unitPrice, BigDecimal totalAmount, LocalDate purchaseDate,
                    String supplier, String notes, String createdBy) {
        this.referenceNumber = referenceNumber;
        this.asset = asset;
        this.assetName = assetName;
        this.category = category;
        this.equipmentType = equipmentType;
        this.base = base;
        this.quantity = quantity;
        this.unit = unit != null ? unit : "Units";
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.purchaseDate = purchaseDate;
        this.supplier = supplier;
        this.notes = notes;
        this.createdBy = createdBy;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.totalAmount == null && this.unitPrice != null && this.quantity != null) {
            this.totalAmount = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
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

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
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

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
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
