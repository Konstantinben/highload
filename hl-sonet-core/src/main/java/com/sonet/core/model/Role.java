package com.sonet.core.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    USER(Set.of(Permission.USERS_READ, Permission.USERS_WRITE)),
    ADMIN(Set.of(Permission.USERS_READ, Permission.USERS_WRITE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> result = new HashSet<>();
        result.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        result.addAll(getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet()));
        return result;
    }
}
