package com.evaap.security;

import com.evaap.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Wraps our User entity for Spring Security.
 * Also implements OAuth2User so it works for both password-based
 * and Google OAuth2 login paths — Spring Security needs one consistent
 * principal type across both.
 */
@Getter
public class CustomUserDetails implements UserDetails,
        org.springframework.security.oauth2.core.user.OAuth2User {

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

    // OAuth2User interface — required so Spring Security accepts this as a principal
    // in the OAuth2 login flow as well as the JWT filter flow.
    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("email", email, "userId", userId, "role", roleName);
    }

    @Override
    public String getName() {
        return email;
    }
}
