package com.example.CafeManagerCore.DTO;

public record CartItemRequestDTO(
     Long cartId,
     Long dishId,
     int count
){}
