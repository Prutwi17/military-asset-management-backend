package com.military.assetmanagement.dto;

import java.time.LocalDate;
import java.util.List;

public class DashboardStatsDto {
    private int openingBalance;
    private int closingBalance;
    private int netMovement;
    private int purchasesQuantity;
    private int transfersInQuantity;
    private int transfersOutQuantity;
    private int assignedQuantity;
    private int expendedQuantity;
    private int totalTrackedAssets;
    private int totalCurrentQuantity;
    private double readinessRate;

    private Long baseId;
    private String baseName;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<StatusCountDto> statusBreakdown;
    private List<CategoryDistributionDto> categoryDistribution;
    private List<AuditLogDto> recentActivities;

    public DashboardStatsDto() {
    }

    public int getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(int openingBalance) {
        this.openingBalance = openingBalance;
    }

    public int getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(int closingBalance) {
        this.closingBalance = closingBalance;
    }

    public int getNetMovement() {
        return netMovement;
    }

    public void setNetMovement(int netMovement) {
        this.netMovement = netMovement;
    }

    public int getPurchasesQuantity() {
        return purchasesQuantity;
    }

    public void setPurchasesQuantity(int purchasesQuantity) {
        this.purchasesQuantity = purchasesQuantity;
    }

    public int getTransfersInQuantity() {
        return transfersInQuantity;
    }

    public void setTransfersInQuantity(int transfersInQuantity) {
        this.transfersInQuantity = transfersInQuantity;
    }

    public int getTransfersOutQuantity() {
        return transfersOutQuantity;
    }

    public void setTransfersOutQuantity(int transfersOutQuantity) {
        this.transfersOutQuantity = transfersOutQuantity;
    }

    public int getAssignedQuantity() {
        return assignedQuantity;
    }

    public void setAssignedQuantity(int assignedQuantity) {
        this.assignedQuantity = assignedQuantity;
    }

    public int getExpendedQuantity() {
        return expendedQuantity;
    }

    public void setExpendedQuantity(int expendedQuantity) {
        this.expendedQuantity = expendedQuantity;
    }

    public int getTotalTrackedAssets() {
        return totalTrackedAssets;
    }

    public void setTotalTrackedAssets(int totalTrackedAssets) {
        this.totalTrackedAssets = totalTrackedAssets;
    }

    public int getTotalCurrentQuantity() {
        return totalCurrentQuantity;
    }

    public void setTotalCurrentQuantity(int totalCurrentQuantity) {
        this.totalCurrentQuantity = totalCurrentQuantity;
    }

    public double getReadinessRate() {
        return readinessRate;
    }

    public void setReadinessRate(double readinessRate) {
        this.readinessRate = readinessRate;
    }

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<StatusCountDto> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(List<StatusCountDto> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public List<CategoryDistributionDto> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(List<CategoryDistributionDto> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    public List<AuditLogDto> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<AuditLogDto> recentActivities) {
        this.recentActivities = recentActivities;
    }
}
