package com.service.notes.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER,
    ADMIN;

    public SimpleGrantedAuthority getAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + name());
    }
}