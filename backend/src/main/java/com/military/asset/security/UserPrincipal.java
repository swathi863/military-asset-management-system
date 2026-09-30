package com.military.asset.security;

import com.military.asset.entity.Role;
import com.military.asset.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Role role;
    private final Long assignedBaseId;
    private final String fullName;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String username, String password, Role role, Long assignedBaseId, String fullName, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.assignedBaseId = assignedBaseId;
        this.fullName = fullName;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        Long baseId = user.getAssignedBase() != null ? user.getAssignedBase().getId() : null;

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole(),
                baseId,
                user.getFullName(),
                Collections.singletonList(authority)
        );
    }

    public Long getId() { return id; }
    public Role getRole() { return role; }
    public Long getAssignedBaseId() { return assignedBaseId; }
    public String getFullName() { return fullName; }

    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return password; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
