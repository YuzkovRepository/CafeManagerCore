package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.DTO.OrderRequestDTO;
import com.example.CafeManagerCore.DTO.OrderResponseDTO;
import com.example.CafeManagerCore.controller.DishController;
import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.model.Guest;
import com.example.CafeManagerCore.model.Order;
import com.example.CafeManagerCore.repository.GuestRepository;
import com.example.CafeManagerCore.repository.OrderRepository;
import com.example.CafeManagerCore.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    final private OrderRepository orderRepository;
    final private GuestRepository guestRepository;
    private static final Logger logger = LoggerFactory.getLogger(DishController.class);

    public ResponseEntity<OrderResponseDTO> addOrder(@Valid OrderRequestDTO orderRequestDTO){
        Order order = new Order();
        Guest guest = guestRepository.findById(orderRequestDTO.clientId())
                .orElseThrow(() -> new CommonException("Гость с ID " + orderRequestDTO.clientId() + " не найден"));
        order.setGuest(guest);
        order.setTotalAmount(BigDecimal.ZERO);

        orderRepository.save(order);
        logger.info("Создан новый заказ ID: {} для гостя ID: {}",
                order.getId(), order.getGuest().getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new OrderResponseDTO(
                        order.getId(),
                        order.getDateTime(),
                        order.getStatus().name(),
                        order.getGuest().getId(),
                        order.getTotalAmount()
                )
        );
    }
}
