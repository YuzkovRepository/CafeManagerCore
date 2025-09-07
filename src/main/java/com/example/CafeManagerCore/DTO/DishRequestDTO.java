package com.example.CafeManagerCore.DTO;

import com.example.CafeManagerCore.model.DishCategory;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record DishRequestDTO(
        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 1000)
        String description,

        @NotNull
        @Digits(integer = 5, fraction = 2)
        BigDecimal cost,

        @NotNull
        Long dishCategory,

        @NotNull String imageBase64
) {}
