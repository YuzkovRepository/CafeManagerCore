package com.example.CafeManagerCore.repository;

import com.example.CafeManagerCore.model.Guest;
import com.example.CafeManagerCore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByGuestAndStatusIn(Guest guest, List<Order.OrderStatus> statuses);
}
