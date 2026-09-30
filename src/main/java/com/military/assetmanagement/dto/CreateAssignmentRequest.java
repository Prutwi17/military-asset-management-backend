package com.military.assetmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateAssignmentRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotBlank(message = "Personnel name is required")
    private String personnelName;

    private String personnelRank;

    @NotBlank(message = "Personnel military ID is required")
    private String personnelId;

    private String unitDivision;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private LocalDate assignmentDate;

    private LocalDate expectedReturnDate;

    private String notes;

    private String referenceNumber;

    public CreateAssignmentRequest() {
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
