package com.example.CafeManagerCore.service;

import com.example.CafeManagerCore.DTO.*;
import jakarta.validation.Valid;

public interface OrderService {
    OrderResponseDTO addOrder(@Valid String phone);
    CartResponseDTO addCart(@Valid String phone);
    CartItemResponseDTO addCartItem(@Valid CartItemRequestDTO cartItemRequestDTO);
    void dropCart(@Valid String phone);
    void dropCartItem(@Valid CartItemDeleteRequestDTO cartItemDeleteRequestDTO);
    CartItemResponseDTO updateQuantityCartItem(UpdateQuantityCartItemRequestDTO updateQuantityCartItemRequestDTO);
    CartResponseDTO getCartByPhone(String phone);
}
