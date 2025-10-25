package com.example.CafeManagerCore.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record OrderResponseDTO (
    Long id,
    LocalDateTime dateTime,
    String status,
    Long guestId,
    BigDecimal totalAmount
){}
