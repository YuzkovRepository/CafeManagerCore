package com.example.CafeManagerCore.DTO;

import jakarta.validation.constraints.NotNull;

public record OrderRequestDTO(
    @NotNull
    Long clientId
){}
