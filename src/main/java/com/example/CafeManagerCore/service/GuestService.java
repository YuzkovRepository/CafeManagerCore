package com.example.CafeManagerCore.service;

import com.example.CafeManagerCore.model.Guest;
import org.springframework.stereotype.Service;

public interface GuestService {
    Guest findOrCreateGuest(String phone);
    Guest getCurrentGuest();
}
