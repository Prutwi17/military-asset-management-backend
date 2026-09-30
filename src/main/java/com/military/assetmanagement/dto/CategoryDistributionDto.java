package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.AssetCategory;

public class CategoryDistributionDto {
    private AssetCategory category;
    private String label;
    private int quantity;
    private int itemCount;
    private double percentage;
    private String color;

    public CategoryDistributionDto() {
    }

    public CategoryDistributionDto(AssetCategory category, String label, int quantity, int itemCount, double percentage, String color) {
        this.category = category;
        this.label = label;
        this.quantity = quantity;
        this.itemCount = itemCount;
        this.percentage = percentage;
        this.color = color;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public void setCategory(AssetCategory category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
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
