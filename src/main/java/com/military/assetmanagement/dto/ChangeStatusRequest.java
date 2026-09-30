package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetStatus;
import jakarta.validation.constraints.NotNull;

public class ChangeStatusRequest {

    @NotNull(message = "Status is required")
    private AssetStatus status;

    private String notes;

    public ChangeStatusRequest() {
    }

    public ChangeStatusRequest(AssetStatus status, String notes) {
        this.status = status;
        this.notes = notes;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
