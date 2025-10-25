package com.example.CafeManagerCore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "dish_categories")
@ToString(exclude = {"dishes"})
@EqualsAndHashCode(exclude = {"dishes"})
public class DishCategory {
    @Id
    @Column(name = "dish_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Digits(integer = 1, fraction = 1)
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Digits(integer = 3, fraction = 1)
    @Column(precision = 4, scale = 1)
    private BigDecimal discount;

    @OneToMany(mappedBy = "dishCategory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Dish> dishes = new HashSet<>();

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
