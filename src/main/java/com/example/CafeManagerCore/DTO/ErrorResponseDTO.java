package com.example.CafeManagerCore.DTO;

public record ErrorResponseDTO(
        int status,
        String message
) {}