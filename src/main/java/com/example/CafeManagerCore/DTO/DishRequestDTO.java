package com.example.CafeManagerCore.DTO;

import com.example.CafeManagerCore.model.DishCategory;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record DishRequestDTO(
        @Column(length = 100)
        String title,

        @Column(columnDefinition = "TEXT")
        String description,

        @Digits(integer = 5, fraction = 2)
        @Column(precision = 7, scale = 2, nullable = false)
        BigDecimal cost,

        @Column(nullable = false)
        Long dishCategory
) {}
