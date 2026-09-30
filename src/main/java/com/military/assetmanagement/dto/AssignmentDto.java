package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.Assignment;
import com.military.assetmanagement.entity.AssignmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssignmentDto {

    private Long id;
    private String referenceNumber;
    private Long assetId;
    private String assetCode;
    private String assetName;
    private AssetCategory category;
    private BaseSummaryDto base;
    private String personnelName;
    private String personnelRank;
    private String personnelId;
    private String unitDivision;
    private Integer quantity;
    private String unit;
    private LocalDate assignmentDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private AssignmentStatus status;
    private String assignedBy;
    private String returnCondition;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AssignmentDto() {
    }

    public static AssignmentDto fromEntity(Assignment a) {
        AssignmentDto dto = new AssignmentDto();
        dto.setId(a.getId());
        dto.setReferenceNumber(a.getReferenceNumber());
        if (a.getAsset() != null) {
            dto.setAssetId(a.getAsset().getId());
            dto.setAssetCode(a.getAsset().getAssetCode());
        }
        dto.setAssetName(a.getAssetName());
        dto.setCategory(a.getCategory());
        dto.setBase(BaseSummaryDto.fromEntity(a.getBase()));
        dto.setPersonnelName(a.getPersonnelName());
        dto.setPersonnelRank(a.getPersonnelRank());
        dto.setPersonnelId(a.getPersonnelId());
        dto.setUnitDivision(a.getUnitDivision());
        dto.setQuantity(a.getQuantity());
        dto.setUnit(a.getUnit());
        dto.setAssignmentDate(a.getAssignmentDate());
        dto.setExpectedReturnDate(a.getExpectedReturnDate());
        dto.setActualReturnDate(a.getActualReturnDate());
        dto.setStatus(a.getStatus());
        dto.setAssignedBy(a.getAssignedBy());
        dto.setReturnCondition(a.getReturnCondition());
        dto.setNotes(a.getNotes());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setUpdatedAt(a.getUpdatedAt());
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

    public BaseSummaryDto getBase() {
        return base;
    }

    public void setBase(BaseSummaryDto base) {
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
