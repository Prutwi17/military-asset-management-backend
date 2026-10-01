package com.military.assetmanagement.service;

import com.military.assetmanagement.dto.AuditLogDto;
import com.military.assetmanagement.entity.AuditLog;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAudit(User user, String action, String entityName, Long entityId,
                            String details, String status, String ipAddress) {
        try {
            String username = user != null ? user.getUsername() : "SYSTEM";
            String fullName = user != null ? user.getFullName() : "Automated System";
            Role role = user != null ? user.getRole() : Role.ADMIN;

            AuditLog auditLog = new AuditLog(
                    username,
                    fullName,
                    role,
                    action,
                    entityName,
                    entityId,
                    details,
                    status != null ? status : "SUCCESS",
                    ipAddress != null ? ipAddress : "127.0.0.1"
            );

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to persist audit log: {}", e.getMessage(), e);
        }
    }

    public void recordAudit(User user, String action, String entityName, Long entityId, String details) {
        recordAudit(user, action, entityName, entityId, details, "SUCCESS", "127.0.0.1");
    }

    @Transactional(readOnly = true)
    public List<AuditLogDto> getAuditLogs(User currentUser, String entityName, String action,
                                          LocalDate startDate, LocalDate endDate, String keyword) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        String effectiveUsername = null;
        Role effectiveRole = null;

        if (currentUser.getRole() != Role.ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException("Access Denied: Only ADMIN can access audit trail logs.");
        }

        List<AuditLog> logs = auditLogRepository.filterAuditLogs(
                entityName, action, effectiveUsername, effectiveRole, start, end, keyword
        );

        return logs.stream().map(AuditLogDto::fromEntity).collect(Collectors.toList());
    }
}
