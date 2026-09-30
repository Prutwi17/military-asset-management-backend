package com.military.assetmanagement.security;

import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final String fullName;
    private final String rank;
    private final Role role;
    private final Long baseId;
    private final String baseName;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.fullName = user.getFullName();
        this.rank = user.getRank();
        this.role = user.getRole();
        if (user.getBase() != null) {
            this.baseId = user.getBase().getId();
            this.baseName = user.getBase().getName();
        } else {
            this.baseId = null;
            this.baseName = null;
        }
        this.active = user.isActive();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRank() {
        return rank;
    }

    public Role getRole() {
        return role;
    }

    public Long getBaseId() {
        return baseId;
    }

    public String getBaseName() {
        return baseName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
