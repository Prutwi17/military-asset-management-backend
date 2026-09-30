package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AuditLog;
import com.military.assetmanagement.entity.Role;

import java.time.LocalDateTime;

public class AuditLogDto {

    private Long id;
    private String username;
    private String userFullName;
    private Role role;
    private String action;
    private String entityName;
    private Long entityId;
    private String details;
    private String status;
    private String ipAddress;
    private LocalDateTime timestamp;

    public AuditLogDto() {
    }

    public static AuditLogDto fromEntity(AuditLog log) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(log.getId());
        dto.setUsername(log.getUsername());
        dto.setUserFullName(log.getUserFullName());
        dto.setRole(log.getRole());
        dto.setAction(log.getAction());
        dto.setEntityName(log.getEntityName());
        dto.setEntityId(log.getEntityId());
        dto.setDetails(log.getDetails());
        dto.setStatus(log.getStatus());
        dto.setIpAddress(log.getIpAddress());
        dto.setTimestamp(log.getTimestamp());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
