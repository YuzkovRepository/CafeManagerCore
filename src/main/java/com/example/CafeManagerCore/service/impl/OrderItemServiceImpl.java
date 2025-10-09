package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.DTO.OrderItemRequestDTO;
import com.example.CafeManagerCore.DTO.OrderItemResponseDTO;
import com.example.CafeManagerCore.DTO.OrderResponseDTO;
import com.example.CafeManagerCore.controller.DishController;
import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.model.Dish;
import com.example.CafeManagerCore.model.Order;
import com.example.CafeManagerCore.model.OrderItem;
import com.example.CafeManagerCore.repository.DishRepository;
import com.example.CafeManagerCore.repository.OrderItemRepository;
import com.example.CafeManagerCore.repository.OrderRepository;
import com.example.CafeManagerCore.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    final private OrderItemRepository orderItemRepository;
    final private DishRepository dishRepository;
    final private OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(DishController.class);

    public ResponseEntity<OrderItemResponseDTO> addOrderItem(@Valid OrderItemRequestDTO orderItemRequestDTO){
        Order order = orderRepository.findById(orderItemRequestDTO.orderId())
                .orElseThrow(() -> new CommonException("Заказ с ID " + orderItemRequestDTO.orderId() + " не найден"));

        Dish dish = dishRepository.findById(orderItemRequestDTO.dishId())
                .orElseThrow(() -> new CommonException("Блюдо с ID " + orderItemRequestDTO.dishId() + " не найдено"));

        if (orderItemRequestDTO.count() <= 0) {
            throw new CommonException("Количество блюда должно быть больше 0");
        }

        if (order.getStatus() != Order.OrderStatus.DRAFT && order.getStatus() != Order.OrderStatus.ACCEPTED) {
            throw new CommonException("Нельзя добавлять items в заказ со статусом: " + order.getStatus());
        }

        boolean dishAlreadyInOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getDish().getId().equals(dish.getId()));
        if (dishAlreadyInOrder) {
            throw new CommonException("Блюдо уже есть в заказе");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setDish(dish);
        orderItem.setCount(orderItemRequestDTO.count());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        order.recalculateTotalAmount();
        orderRepository.save(order);

        logger.info("Добавлен элемент заказа ID: {} в заказ ID: {}. Блюдо: {}, количество: {}",
                savedOrderItem.getId(), order.getId(), dish.getTitle(), orderItemRequestDTO.count());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new OrderItemResponseDTO(
                        savedOrderItem.getId(),
                        savedOrderItem.getDish().getId(),
                        savedOrderItem.getDish().getTitle(),
                        savedOrderItem.getCount(),
                        savedOrderItem.getUnitPrice(),
                        savedOrderItem.getTotalPrice()
                )
        );
    }
}
