package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.Assignment;
import com.military.assetmanagement.entity.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    boolean existsByReferenceNumber(String referenceNumber);

    @Query("SELECT a FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:startDate IS NULL OR a.assignmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.assignmentDate <= :endDate) AND " +
           "(:keyword IS NULL OR " +
           " LOWER(a.personnelName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.personnelId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.assetName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.referenceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(a.unitDivision) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY a.assignmentDate DESC")
    List<Assignment> filterAssignments(
            @Param("baseId") Long baseId,
            @Param("status") AssignmentStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword
    );
}
