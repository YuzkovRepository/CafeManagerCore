package com.example.CafeManagerCore.controller;

import com.example.CafeManagerCore.DTO.*;
import com.example.CafeManagerCore.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(DishController.class);

    @GetMapping("/{phone}")
    public ResponseEntity<CartResponseDTO> getCartByPhone(
            @PathVariable @Pattern(regexp = "^\\+?[7-8]?[0-9]{10}$") String phone) {
        CartResponseDTO cart = orderService.getCartByPhone(phone);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add/{phone}")
    public ResponseEntity<CartResponseDTO> addCart(
            @PathVariable @Pattern(regexp = "^\\+?[7-8]?[0-9]{10}$") String phone) {

        logger.info("Received cart creation request for phone: {}", phone);

        try {
            CartResponseDTO response = orderService.addCart(phone);
            logger.info("✅ Cart created successfully: {}", response);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("❌ Error creating cart: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/add/items")
    public ResponseEntity<CartItemResponseDTO> addCartItem(@RequestBody @Valid CartItemRequestDTO cartItemRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addCartItem(cartItemRequestDTO));
    }

    @DeleteMapping("/drop/{phone}")
    public ResponseEntity<Void> dropCart(
            @PathVariable @Pattern(regexp = "^\\+?[7-8]?[0-9]{10}$") String phone) {

        orderService.dropCart(phone);
        logger.info("The user's shopping cart with the number {} deleted.", phone);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/drop/items")
    public ResponseEntity<Void> dropCartItem(
            @RequestBody @Valid CartItemDeleteRequestDTO request) {

        orderService.dropCartItem(request);
        logger.info("The user's shopping cart item has been deleted.");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/items")
    public ResponseEntity<CartItemResponseDTO> updateQuantityCartItem(
            @RequestBody @Valid UpdateQuantityCartItemRequestDTO request) {

        CartItemResponseDTO response = orderService.updateQuantityCartItem(request);
        logger.info("The number of dishes in the basket has been changed.");
        return ResponseEntity.ok(response);
    }
}
