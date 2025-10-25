package com.example.CafeManagerCore.repository;

import com.example.CafeManagerCore.model.Cart;
import com.example.CafeManagerCore.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.dish WHERE c.guest = :guest")
    Optional<Cart> findByGuestWithItemsAndDishes(@Param("guest") Guest guest);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.dish WHERE c.id = :id")
    Optional<Cart> findWithCartItemsAndDishesById(@Param("id") Long id);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.guest.phone = :phone")
    Optional<Cart> findByGuestPhoneWithItems(@Param("phone") String phone);

    @Query("SELECT c FROM Cart c " +
            "LEFT JOIN FETCH c.cartItems ci " +
            "LEFT JOIN FETCH ci.dish " +
            "WHERE c.guest.phone = :phone")
    Optional<Cart> findByGuestPhoneWithItemsAndDishes(@Param("phone") String phone);

    @Query("SELECT c FROM Cart c WHERE c.guest.phone = :phone")
    Optional<Cart> findByGuestPhone(@Param("phone") String phone);

    // Для загрузки корзины со всеми элементами
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems WHERE c.id = :id")
    Optional<Cart> findWithCartItemsById(@Param("id") Long id);
}
