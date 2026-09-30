package com.military.assetmanagement.dto;

import java.time.LocalDate;
import java.util.List;

public class NetMovementResponseDto {
    private int totalPurchases;
    private int totalTransfersIn;
    private int totalTransfersOut;
    private int netMovement;

    private Long baseId;
    private String baseName;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<NetMovementItemDto> items;

    public NetMovementResponseDto() {
    }

    public int getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(int totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public int getTotalTransfersIn() {
        return totalTransfersIn;
    }

    public void setTotalTransfersIn(int totalTransfersIn) {
        this.totalTransfersIn = totalTransfersIn;
    }

    public int getTotalTransfersOut() {
        return totalTransfersOut;
    }

    public void setTotalTransfersOut(int totalTransfersOut) {
        this.totalTransfersOut = totalTransfersOut;
    }

    public int getNetMovement() {
        return netMovement;
    }

    public void setNetMovement(int netMovement) {
        this.netMovement = netMovement;
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

    public List<NetMovementItemDto> getItems() {
        return items;
    }

    public void setItems(List<NetMovementItemDto> items) {
        this.items = items;
    }
}
