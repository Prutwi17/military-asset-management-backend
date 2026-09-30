package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.Base;

import java.time.LocalDateTime;

public class BaseDto {
    private Long id;
    private String name;
    private String code;
    private String location;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BaseDto() {
    }

    public static BaseDto fromEntity(Base base) {
        if (base == null) return null;
        BaseDto dto = new BaseDto();
        dto.setId(base.getId());
        dto.setName(base.getName());
        dto.setCode(base.getCode());
        dto.setLocation(base.getLocation());
        dto.setDescription(base.getDescription());
        dto.setStatus(base.getStatus());
        dto.setCreatedAt(base.getCreatedAt());
        dto.setUpdatedAt(base.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
