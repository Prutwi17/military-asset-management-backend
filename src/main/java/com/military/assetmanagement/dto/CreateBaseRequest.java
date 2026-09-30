package com.military.assetmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateBaseRequest {

    @NotBlank(message = "Base name is required")
    @Size(max = 100, message = "Base name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Base code is required")
    @Size(max = 50, message = "Base code must not exceed 50 characters")
    private String code;

    private String location;
    private String description;
    private String status = "ACTIVE";

    public CreateBaseRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
