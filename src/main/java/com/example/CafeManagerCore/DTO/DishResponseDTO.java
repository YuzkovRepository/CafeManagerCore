package com.example.CafeManagerCore.DTO;

import java.math.BigDecimal;

public record DishResponseDTO(
        Long id,
        String title,
        String description,
        BigDecimal cost,
        BigDecimal rating,
        BigDecimal discount,
        String dishCategory,
        String imageBase64
) {}
