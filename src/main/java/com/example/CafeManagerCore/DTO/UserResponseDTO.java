package com.example.CafeManagerCore.DTO;

public record UserResponseDTO(
        String login,
        String password,
        String email,
        String phone,
        String status
) {}
