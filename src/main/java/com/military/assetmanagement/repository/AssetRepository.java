package com.military.assetmanagement.repository;

import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.AssetCategory;
import com.military.assetmanagement.entity.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetCode(String assetCode);

    Optional<Asset> findBySerialNumber(String serialNumber);

    boolean existsByAssetCode(String assetCode);

    boolean existsBySerialNumber(String serialNumber);

    List<Asset> findByBaseIdAndActiveTrue(Long baseId);

    @Query("SELECT a FROM Asset a WHERE a.active = true " +
           "AND (:baseId IS NULL OR a.base.id = :baseId) " +
           "AND (:category IS NULL OR a.category = :category) " +
           "AND (:status IS NULL OR a.status = :status) " +
           "AND (:equipmentType IS NULL OR LOWER(a.equipmentType) LIKE LOWER(CONCAT('%', :equipmentType, '%'))) " +
           "AND (:keyword IS NULL OR (LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.assetCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(a.location) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
           "ORDER BY a.id ASC")
    List<Asset> filterAssets(
            @Param("baseId") Long baseId,
            @Param("category") AssetCategory category,
            @Param("status") AssetStatus status,
            @Param("equipmentType") String equipmentType,
            @Param("keyword") String keyword
    );
}
