package com.example.CafeManagerCore.DTO;

import java.time.LocalDateTime;

public record GuestResponseDTO(
        Long guestId,
        String phone,
        LocalDateTime dateRegistration
) {}