package com.example.CafeManagerCore.repository;

import com.example.CafeManagerCore.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    @Query("SELECT g FROM Guest g LEFT JOIN FETCH g.cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.dish WHERE g.phone = :phone")
    Optional<Guest> findByPhoneWithCartAndItems (String phone);
    Optional<Guest> findByPhone(String phone);
}
