package com.example.CafeManagerCore.securityUser;

import com.example.CafeManagerCore.model.Guest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class GuestAuthenticationToken extends AbstractAuthenticationToken {
    private final String phone;
    private final Guest guest;

    public GuestAuthenticationToken(String phone, Guest guest) {
        super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST_ROLE")));
        this.phone = phone;
        this.guest = guest;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return guest;
    }

    public String getPhone() {
        return phone;
    }

    public Guest getGuest() {
        return guest;
    }
}