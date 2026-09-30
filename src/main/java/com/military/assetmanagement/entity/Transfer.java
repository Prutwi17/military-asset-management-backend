package com.military.assetmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
public class Transfer {

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

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 30)
    private String unit = "Units";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_base_id", nullable = false)
    private Base sourceBase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_base_id", nullable = false)
    private Base destinationBase;

    @Column(name = "transfer_date", nullable = false)
    private LocalDate transferDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransferStatus status = TransferStatus.PENDING;

    @Column(name = "requested_by", nullable = false, length = 100)
    private String requestedBy;

    @Column(name = "requested_by_user_id")
    private Long requestedByUserId;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_by_user_id")
    private Long approvedByUserId;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(length = 1000)
    private String notes;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public Transfer() {
    }

    public Transfer(String referenceNumber, Asset asset, String assetName, AssetCategory category,
                    String equipmentType, Integer quantity, String unit, Base sourceBase,
                    Base destinationBase, LocalDate transferDate, TransferStatus status,
                    String requestedBy, Long requestedByUserId, String reason, String notes) {
        this.referenceNumber = referenceNumber;
        this.asset = asset;
        this.assetName = assetName;
        this.category = category;
        this.equipmentType = equipmentType;
        this.quantity = quantity;
        this.unit = unit != null ? unit : "Units";
        this.sourceBase = sourceBase;
        this.destinationBase = destinationBase;
        this.transferDate = transferDate != null ? transferDate : LocalDate.now();
        this.status = status != null ? status : TransferStatus.PENDING;
        this.requestedBy = requestedBy;
        this.requestedByUserId = requestedByUserId;
        this.reason = reason;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public Base getSourceBase() {
        return sourceBase;
    }

    public void setSourceBase(Base sourceBase) {
        this.sourceBase = sourceBase;
    }

    public Base getDestinationBase() {
        return destinationBase;
    }

    public void setDestinationBase(Base destinationBase) {
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
