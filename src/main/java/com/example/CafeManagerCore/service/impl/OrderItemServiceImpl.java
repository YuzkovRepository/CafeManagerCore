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

}
