package com.military.assetmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateExpenditureRequest {

    @NotNull(message = "Asset ID is required")
    private Long assetId;

    @NotNull(message = "Base ID is required")
    private Long baseId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String unit;

    @NotBlank(message = "Personnel or operating unit is required")
    private String personnelOrUnit;

    private LocalDate expenditureDate;

    @NotBlank(message = "Expenditure reason is required")
    private String reason;

    private String reference;

    private String notes;

    private String referenceNumber;

    public CreateExpenditureRequest() {
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
