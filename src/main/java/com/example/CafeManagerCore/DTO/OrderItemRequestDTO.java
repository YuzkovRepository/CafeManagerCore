package com.example.CafeManagerCore.DTO;

public record OrderItemRequestDTO (
     Long orderId,
     Long dishId,
     int count
){}
