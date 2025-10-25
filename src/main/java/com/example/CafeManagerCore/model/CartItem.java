package com.example.CafeManagerCore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "cart_items")
@ToString(exclude = {"cart", "dish"})
@EqualsAndHashCode(exclude = {"cart", "dish"})
public class CartItem {
    @Id
    @Column(name = "cart_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    private int count;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(count));
    }

    @PrePersist
    protected void onPrePersist() {
        if (unitPrice == null && dish != null) {
            unitPrice = dish.getCost();
        }
    }
}
