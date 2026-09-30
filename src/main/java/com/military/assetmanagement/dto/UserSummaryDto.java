package com.military.assetmanagement.dto;

import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.User;

public class UserSummaryDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String rank;
    private Role role;
    private BaseSummaryDto base;
    private boolean active;

    public UserSummaryDto() {
    }

    public static UserSummaryDto fromEntity(User user) {
        UserSummaryDto dto = new UserSummaryDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRank(user.getRank());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        if (user.getBase() != null) {
            dto.setBase(new BaseSummaryDto(
                user.getBase().getId(),
                user.getBase().getName(),
                user.getBase().getCode(),
                user.getBase().getLocation()
            ));
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public BaseSummaryDto getBase() {
        return base;
    }

    public void setBase(BaseSummaryDto base) {
        this.base = base;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
