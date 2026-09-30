package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetCategory;

import java.math.BigDecimal;
import java.util.List;

public class ReportsDto {

    public static class BaseInventorySummaryDto {
        private Long baseId;
        private String baseName;
        private String baseCode;
        private String location;
        private int totalAssetsCount;
        private int totalQuantity;
        private BigDecimal totalValuation;
        private int activeAssignments;

        public BaseInventorySummaryDto() {
        }

        public BaseInventorySummaryDto(Long baseId, String baseName, String baseCode, String location,
                                      int totalAssetsCount, int totalQuantity, BigDecimal totalValuation, int activeAssignments) {
            this.baseId = baseId;
            this.baseName = baseName;
            this.baseCode = baseCode;
            this.location = location;
            this.totalAssetsCount = totalAssetsCount;
            this.totalQuantity = totalQuantity;
            this.totalValuation = totalValuation != null ? totalValuation : BigDecimal.ZERO;
            this.activeAssignments = activeAssignments;
        }

        public Long getBaseId() { return baseId; }
        public void setBaseId(Long baseId) { this.baseId = baseId; }
        public String getBaseName() { return baseName; }
        public void setBaseName(String baseName) { this.baseName = baseName; }
        public String getBaseCode() { return baseCode; }
        public void setBaseCode(String baseCode) { this.baseCode = baseCode; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public int getTotalAssetsCount() { return totalAssetsCount; }
        public void setTotalAssetsCount(int totalAssetsCount) { this.totalAssetsCount = totalAssetsCount; }
        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
        public BigDecimal getTotalValuation() { return totalValuation; }
        public void setTotalValuation(BigDecimal totalValuation) { this.totalValuation = totalValuation; }
        public int getActiveAssignments() { return activeAssignments; }
        public void setActiveAssignments(int activeAssignments) { this.activeAssignments = activeAssignments; }
    }

    public static class EquipmentDistributionSummaryDto {
        private AssetCategory category;
        private String categoryName;
        private int totalQuantity;
        private int totalAssetTypes;
        private BigDecimal totalValuation;
        private double percentage;

        public EquipmentDistributionSummaryDto() {
        }

        public EquipmentDistributionSummaryDto(AssetCategory category, String categoryName, int totalQuantity,
                                               int totalAssetTypes, BigDecimal totalValuation, double percentage) {
            this.category = category;
            this.categoryName = categoryName;
            this.totalQuantity = totalQuantity;
            this.totalAssetTypes = totalAssetTypes;
            this.totalValuation = totalValuation != null ? totalValuation : BigDecimal.ZERO;
            this.percentage = percentage;
        }

        public AssetCategory getCategory() { return category; }
        public void setCategory(AssetCategory category) { this.category = category; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
        public int getTotalAssetTypes() { return totalAssetTypes; }
        public void setTotalAssetTypes(int totalAssetTypes) { this.totalAssetTypes = totalAssetTypes; }
        public BigDecimal getTotalValuation() { return totalValuation; }
        public void setTotalValuation(BigDecimal totalValuation) { this.totalValuation = totalValuation; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    public static class AssetUtilizationSummaryDto {
        private int totalAssetTypes;
        private int totalQuantity;
        private int availableQuantity;
        private int inUseQuantity;
        private int maintenanceQuantity;
        private int deployedQuantity;
        private int decommissionedQuantity;
        private double combatReadinessRate;

        public AssetUtilizationSummaryDto() {
        }

        public int getTotalAssetTypes() { return totalAssetTypes; }
        public void setTotalAssetTypes(int totalAssetTypes) { this.totalAssetTypes = totalAssetTypes; }
        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
        public int getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
        public int getInUseQuantity() { return inUseQuantity; }
        public void setInUseQuantity(int inUseQuantity) { this.inUseQuantity = inUseQuantity; }
        public int getMaintenanceQuantity() { return maintenanceQuantity; }
        public void setMaintenanceQuantity(int maintenanceQuantity) { this.maintenanceQuantity = maintenanceQuantity; }
        public int getDeployedQuantity() { return deployedQuantity; }
        public void setDeployedQuantity(int deployedQuantity) { this.deployedQuantity = deployedQuantity; }
        public int getDecommissionedQuantity() { return decommissionedQuantity; }
        public void setDecommissionedQuantity(int decommissionedQuantity) { this.decommissionedQuantity = decommissionedQuantity; }
        public double getCombatReadinessRate() { return combatReadinessRate; }
        public void setCombatReadinessRate(double combatReadinessRate) { this.combatReadinessRate = combatReadinessRate; }
    }

    public static class FullReportsResponseDto {
        private AssetUtilizationSummaryDto utilization;
        private List<BaseInventorySummaryDto> baseInventory;
        private List<EquipmentDistributionSummaryDto> equipmentDistribution;
        private List<PurchaseDto> purchaseHistory;
        private List<TransferDto> transferHistory;
        private List<AssignmentDto> assignmentHistory;
        private List<ExpenditureDto> expenditureHistory;

        public FullReportsResponseDto() {
        }

        public AssetUtilizationSummaryDto getUtilization() { return utilization; }
        public void setUtilization(AssetUtilizationSummaryDto utilization) { this.utilization = utilization; }
        public List<BaseInventorySummaryDto> getBaseInventory() { return baseInventory; }
        public void setBaseInventory(List<BaseInventorySummaryDto> baseInventory) { this.baseInventory = baseInventory; }
        public List<EquipmentDistributionSummaryDto> getEquipmentDistribution() { return equipmentDistribution; }
        public void setEquipmentDistribution(List<EquipmentDistributionSummaryDto> equipmentDistribution) { this.equipmentDistribution = equipmentDistribution; }
        public List<PurchaseDto> getPurchaseHistory() { return purchaseHistory; }
        public void setPurchaseHistory(List<PurchaseDto> purchaseHistory) { this.purchaseHistory = purchaseHistory; }
        public List<TransferDto> getTransferHistory() { return transferHistory; }
        public void setTransferHistory(List<TransferDto> transferHistory) { this.transferHistory = transferHistory; }
        public List<AssignmentDto> getAssignmentHistory() { return assignmentHistory; }
        public void setAssignmentHistory(List<AssignmentDto> assignmentHistory) { this.assignmentHistory = assignmentHistory; }
        public List<ExpenditureDto> getExpenditureHistory() { return expenditureHistory; }
        public void setExpenditureHistory(List<ExpenditureDto> expenditureHistory) { this.expenditureHistory = expenditureHistory; }
    }
}
