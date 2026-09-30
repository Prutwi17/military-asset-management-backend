package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetStatus;

public class StatusCountDto {
    private AssetStatus status;
    private String label;
    private int count;
    private double percentage;
    private String color;

    public StatusCountDto() {
    }

    public StatusCountDto(AssetStatus status, String label, int count, double percentage, String color) {
        this.status = status;
        this.label = label;
        this.count = count;
        this.percentage = percentage;
        this.color = color;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
