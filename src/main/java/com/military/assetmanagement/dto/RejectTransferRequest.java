package com.military.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectTransferRequest {

    @NotBlank(message = "Rejection reason is required")
    private String reason;

    public RejectTransferRequest() {
    }

    public RejectTransferRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
