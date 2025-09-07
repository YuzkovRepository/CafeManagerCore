package com.example.CafeManagerCore.model;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "dishes")
public class Dish {
    @Id
    @Column(name = "dish_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Digits(integer = 5, fraction = 2)
    @Column(precision = 7, scale = 2, nullable = false)
    private BigDecimal cost;

    @Digits(integer = 1, fraction = 1)
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Digits(integer = 3, fraction = 1)
    @Column(precision = 4, scale = 1)
    private BigDecimal discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_category", nullable = false)
    private DishCategory dishCategory;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "dish", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (discount == null) {
            discount = BigDecimal.valueOf(5);
        }

        if (rating == null){
            rating = BigDecimal.valueOf(0);
        }
    }
}
