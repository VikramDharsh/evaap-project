package com.evaap.security;

import com.evaap.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Wraps our User entity so Spring Security can work with it.
 * Authorities are prefixed with ROLE_ (Spring Security convention) using
 * the role_name straight from the roles table, e.g. ROLE_CANDIDATE.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String passwordHash;
    private final String roleName;
    private final boolean active;
    private final boolean emailVerified;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.roleName = user.getRole().getRoleName();
        this.active = Boolean.TRUE.equals(user.getIsActive())
                && user.getAccountStatus() != User.AccountStatus.SUSPENDED
                && user.getAccountStatus() != User.AccountStatus.DEACTIVATED
                && user.getDeletedAt() == null;
        this.emailVerified = user.getEmailVerifiedAt() != null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
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
