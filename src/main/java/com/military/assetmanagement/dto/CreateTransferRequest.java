package com.military.assetmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class CreateTransferRequest {

    @NotNull(message = "Source asset ID is required")
    private Long assetId;

    @NotNull(message = "Destination base ID is required")
    private Long destinationBaseId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private LocalDate transferDate;

    @NotBlank(message = "Transfer reason is required")
    private String reason;

    private String notes;

    private String referenceNumber;

    public CreateTransferRequest() {
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public Long getDestinationBaseId() {
        return destinationBaseId;
    }

    public void setDestinationBaseId(Long destinationBaseId) {
        this.destinationBaseId = destinationBaseId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
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

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
