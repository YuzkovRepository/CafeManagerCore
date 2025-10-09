package com.example.CafeManagerCore.DTO;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
      Long orderId,
      Long dishId,
      String dishTitle,
      int count,
      BigDecimal unitPrice,
      BigDecimal totalPrice
){}
