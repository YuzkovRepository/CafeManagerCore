package com.example.CafeManagerCore.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CartRequestDTO(
        @NotNull
        @Pattern(regexp = "^\\+?[7-8]?[0-9]{10}$", message = "Неверный формат телефона")
        String phone
) {}
