package com.example.CafeManagerCore.DTO;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO(
        @NotBlank(message = "Login or Email cannot be null or empty") String loginOrEmail,
        String password
) {}