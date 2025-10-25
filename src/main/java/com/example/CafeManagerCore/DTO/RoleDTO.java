package com.example.CafeManagerCore.DTO;

public record RoleDTO(
        String roleName,
        Long parentRoleId,
        String description
) {}