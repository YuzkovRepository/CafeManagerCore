package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.repository.GuestRepository;
import com.example.CafeManagerCore.service.GuestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GuestServiceImpl implements GuestService {
    private final GuestRepository guestRepository;

}
