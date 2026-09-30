package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByReferenceNumber(String referenceNumber);

    boolean existsByReferenceNumber(String referenceNumber);

    List<Purchase> findByBaseId(Long baseId);

    @Query("SELECT p FROM Purchase p WHERE (:baseId IS NULL OR p.base.id = :baseId) " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:startDate IS NULL OR p.purchaseDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.purchaseDate <= :endDate) " +
           "AND (:keyword IS NULL OR (LOWER(p.assetName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(p.referenceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(p.supplier) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(p.equipmentType) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "ORDER BY p.purchaseDate DESC, p.id DESC")
    List<Purchase> filterPurchases(
            @Param("baseId") Long baseId,
            @Param("category") AssetCategory category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword
    );
}
