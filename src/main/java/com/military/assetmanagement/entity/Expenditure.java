package com.military.assetmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenditures")
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_id", nullable = false)
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

    @Column(name = "personnel_or_unit", nullable = false, length = 150)
    private String personnelOrUnit;

    @Column(name = "expenditure_date", nullable = false)
    private LocalDate expenditureDate;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(name = "operation_reference", length = 100)
    private String reference;

    @Column(name = "recorded_by", nullable = false, length = 100)
    private String recordedBy;

    @Column(name = "recorded_by_user_id")
    private Long recordedByUserId;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Expenditure() {
    }

    public Expenditure(String referenceNumber, Asset asset, String assetName, AssetCategory category,
                       String equipmentType, Base base, Integer quantity, String unit,
                       String personnelOrUnit, LocalDate expenditureDate, String reason,
                       String reference, String recordedBy, Long recordedByUserId, String notes) {
        this.referenceNumber = referenceNumber;
        this.asset = asset;
        this.assetName = assetName;
        this.category = category;
        this.equipmentType = equipmentType;
        this.base = base;
        this.quantity = quantity;
        this.unit = unit != null ? unit : "Units";
        this.personnelOrUnit = personnelOrUnit;
        this.expenditureDate = expenditureDate != null ? expenditureDate : LocalDate.now();
        this.reason = reason;
        this.reference = reference;
        this.recordedBy = recordedBy;
        this.recordedByUserId = recordedByUserId;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
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
