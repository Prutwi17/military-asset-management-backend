package com.military.assetmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_assignments")
public class Assignment {

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_id", nullable = false)
    private Base base;

    @Column(name = "personnel_name", nullable = false, length = 100)
    private String personnelName;

    @Column(name = "personnel_rank", length = 50)
    private String personnelRank;

    @Column(name = "personnel_id", nullable = false, length = 50)
    private String personnelId;

    @Column(name = "unit_division", length = 100)
    private String unitDivision;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 30)
    private String unit = "Units";

    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate;

    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssignmentStatus status = AssignmentStatus.ACTIVE;

    @Column(name = "assigned_by", nullable = false, length = 100)
    private String assignedBy;

    @Column(name = "return_condition", length = 50)
    private String returnCondition;

    @Column(length = 1000)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Assignment() {
    }

    public Assignment(String referenceNumber, Asset asset, String assetName, AssetCategory category,
                      Base base, String personnelName, String personnelRank, String personnelId,
                      String unitDivision, Integer quantity, String unit, LocalDate assignmentDate,
                      LocalDate expectedReturnDate, AssignmentStatus status, String assignedBy, String notes) {
        this.referenceNumber = referenceNumber;
        this.asset = asset;
        this.assetName = assetName;
        this.category = category;
        this.base = base;
        this.personnelName = personnelName;
        this.personnelRank = personnelRank;
        this.personnelId = personnelId;
        this.unitDivision = unitDivision;
        this.quantity = quantity;
        this.unit = unit != null ? unit : "Units";
        this.assignmentDate = assignmentDate != null ? assignmentDate : LocalDate.now();
        this.expectedReturnDate = expectedReturnDate;
        this.status = status != null ? status : AssignmentStatus.ACTIVE;
        this.assignedBy = assignedBy;
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

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public String getPersonnelName() {
        return personnelName;
    }

    public void setPersonnelName(String personnelName) {
        this.personnelName = personnelName;
    }

    public String getPersonnelRank() {
        return personnelRank;
    }

    public void setPersonnelRank(String personnelRank) {
        this.personnelRank = personnelRank;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public String getUnitDivision() {
        return unitDivision;
    }

    public void setUnitDivision(String unitDivision) {
        this.unitDivision = unitDivision;
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

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getReturnCondition() {
        return returnCondition;
    }

    public void setReturnCondition(String returnCondition) {
        this.returnCondition = returnCondition;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
