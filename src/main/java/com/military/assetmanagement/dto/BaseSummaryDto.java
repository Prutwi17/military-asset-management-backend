package com.military.assetmanagement.dto;

public class BaseSummaryDto {
    private Long id;
    private String name;
    private String code;
    private String location;

    public BaseSummaryDto() {
    }

    public BaseSummaryDto(Long id, String name, String code, String location) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.location = location;
    }

    public static BaseSummaryDto fromEntity(com.military.assetmanagement.entity.Base base) {
        if (base == null) return null;
        return new BaseSummaryDto(base.getId(), base.getName(), base.getCode(), base.getLocation());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
