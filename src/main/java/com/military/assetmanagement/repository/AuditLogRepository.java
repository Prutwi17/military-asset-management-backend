package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.AuditLog;
import com.military.assetmanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityName IS NULL OR a.entityName = :entityName) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:username IS NULL OR a.username = :username) AND " +
           "(:role IS NULL OR a.role = :role) AND " +
           "(:startDate IS NULL OR a.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR a.timestamp <= :endDate) AND " +
           "(:keyword IS NULL OR LOWER(a.details) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.userFullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.timestamp DESC")
    List<AuditLog> filterAuditLogs(
            @Param("entityName") String entityName,
            @Param("action") String action,
            @Param("username") String username,
            @Param("role") Role role,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keyword") String keyword
    );

    List<AuditLog> findTop100ByOrderByTimestampDesc();
}
