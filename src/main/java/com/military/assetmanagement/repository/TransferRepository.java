package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.Transfer;
import com.military.assetmanagement.entity.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    boolean existsByReferenceNumber(String referenceNumber);

    @Query("SELECT t FROM Transfer t WHERE " +
           "(:sourceBaseId IS NULL OR t.sourceBase.id = :sourceBaseId) AND " +
           "(:destinationBaseId IS NULL OR t.destinationBase.id = :destinationBaseId) AND " +
           "(:eitherBaseId IS NULL OR t.sourceBase.id = :eitherBaseId OR t.destinationBase.id = :eitherBaseId) AND " +
           "(:category IS NULL OR t.category = :category) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate) AND " +
           "(:keyword IS NULL OR " +
           " LOWER(t.assetName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(t.referenceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(t.reason) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(t.requestedBy) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<Transfer> filterTransfers(
            @Param("sourceBaseId") Long sourceBaseId,
            @Param("destinationBaseId") Long destinationBaseId,
            @Param("eitherBaseId") Long eitherBaseId,
            @Param("category") AssetCategory category,
            @Param("status") TransferStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword
    );
}
