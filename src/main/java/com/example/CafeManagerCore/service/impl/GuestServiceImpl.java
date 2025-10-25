package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.model.Guest;
import com.example.CafeManagerCore.repository.GuestRepository;
import com.example.CafeManagerCore.securityUser.GuestAuthenticationToken;
import com.example.CafeManagerCore.service.GuestService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;

    public Guest findOrCreateGuest(String phone) {
        return guestRepository.findByPhone(phone)
                .orElseGet(() -> createNewGuest(phone));
    }

    private Guest createNewGuest(String phone) {
        Guest guest = new Guest()
                .setPhone(phone);
        return guestRepository.save(guest);
    }

    public Guest getCurrentGuest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof GuestAuthenticationToken) {
            return ((GuestAuthenticationToken) authentication).getGuest();
        }
        throw new CommonException("Доступ только для гостей");
    }

}
