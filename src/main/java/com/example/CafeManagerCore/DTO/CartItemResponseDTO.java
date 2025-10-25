package com.example.CafeManagerCore.DTO;

import java.math.BigDecimal;

public record CartItemResponseDTO(
      Long cartItemId,
      Long dishId,
      String dishTitle,
      int count,
      BigDecimal unitPrice,
      BigDecimal totalPrice
){}
