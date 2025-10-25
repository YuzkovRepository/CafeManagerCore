package com.example.CafeManagerCore.DTO;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDTO(
        Long cartId,
        Long guestId,
        BigDecimal totalAmount,
        List<CartItemResponseDTO> items
) {}