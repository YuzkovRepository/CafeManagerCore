package com.example.CafeManagerCore.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CartItemDeleteRequestDTO (
    @Pattern(regexp = "^\\+?[7-8]?[0-9]{10}$", message = "Неверный формат телефона")
    @NotNull
    String phone,
    @NotNull
    Long cartItemId
){}
