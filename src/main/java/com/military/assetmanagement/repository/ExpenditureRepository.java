package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    boolean existsByReferenceNumber(String referenceNumber);

    @Query("SELECT e FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:startDate IS NULL OR e.expenditureDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenditureDate <= :endDate) AND " +
           "(:keyword IS NULL OR " +
           " LOWER(e.assetName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.referenceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.personnelOrUnit) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.reason) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(e.reference) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY e.expenditureDate DESC")
    List<Expenditure> filterExpenditures(
            @Param("baseId") Long baseId,
            @Param("category") AssetCategory category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("keyword") String keyword
    );
}
