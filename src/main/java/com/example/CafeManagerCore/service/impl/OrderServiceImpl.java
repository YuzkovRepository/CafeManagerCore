package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.DTO.*;
import com.example.CafeManagerCore.controller.DishController;
import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.model.*;
import com.example.CafeManagerCore.repository.*;
import com.example.CafeManagerCore.service.OrderService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    final private OrderRepository orderRepository;
    final private GuestRepository guestRepository;
    final private OrderItemRepository orderItemRepository;
    final private DishRepository dishRepository;
    final private CartRepository cartRepository;
    final private CartItemRepository cartItemRepository;
    private static final Logger logger = LoggerFactory.getLogger(DishController.class);

    public CartResponseDTO getCartByPhone(String phone) {
        // Находим корзину с элементами и блюдами
        Cart cart = cartRepository.findByGuestPhoneWithItemsAndDishes(phone)
                .orElseThrow(() -> new CommonException("Корзина не найдена"));

        // Маппим в DTO
        List<CartItemResponseDTO> items = cart.getCartItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        return new CartResponseDTO(
                cart.getId(),
                cart.getGuest().getId(),
                cart.getTotalAmount(),
                items
        );
    }


    @Transactional
    public OrderResponseDTO addOrder(@Valid String phone) {
        Guest guest = guestRepository.findByPhoneWithCartAndItems(phone)
                .orElseThrow(() -> new CommonException("Пользователь не найден"));

        Cart cart = cartRepository.findByGuestWithItemsAndDishes(guest)
                .orElseThrow(() -> new CommonException("Корзина не найдена"));

        if (cart.getCartItems().isEmpty()) {
            throw new CommonException("Корзина пуста");
        }

        cart.getCartItems().forEach(item -> {
            if (!item.getDish().isAvailable()) {
                throw new CommonException("Блюдо " + item.getDish().getTitle() + " недоступно");
            }
        });

        if (orderRepository.existsByGuestAndStatusIn(guest,
                List.of(Order.OrderStatus.ACCEPTED, Order.OrderStatus.IN_PROGRESS))) {
            throw new CommonException("У вас уже есть активный заказ");
        }

        Order order = new Order()
                .setGuest(guest)
                .setStatus(Order.OrderStatus.ACCEPTED);

        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            return new OrderItem()
                    .setOrder(order)
                    .setDish(cartItem.getDish())
                    .setCount(cartItem.getCount())
                    .setUnitPrice(cartItem.getDish().getCost());
        }).toList();

        order.setOrderItems(new ArrayList<>(orderItems));
        order.setTotalAmount(cart.getTotalAmount());

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.delete(cart);

        logger.info("Создан заказ ID: {} для гостя: {}", savedOrder.getId(), guest.getId());

        OrderResponseDTO orderResponseDTO = mapToOrderResponse(savedOrder);
        return orderResponseDTO;
    }

    private OrderResponseDTO mapToOrderResponse(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getDateTime(),
                order.getStatus().name(),
                order.getGuest().getId(),
                order.getTotalAmount()
        );
    }

    @Transactional
    public CartResponseDTO addCart(@Valid String phone) {
        // Проверяем существующую корзину
        Optional<Cart> existingCart = cartRepository.findByGuestPhone(phone);
        if (existingCart.isPresent()) {
            throw new CommonException("Корзина для гостя с телефоном " + phone + " уже существует");
        }

        // Находим гостя
        Guest guest = guestRepository.findByPhone(phone)
                .orElseThrow(() -> new CommonException("Гость с телефоном " + phone + " не найден"));

        // Создаем и сохраняем корзину
        Cart cart = new Cart();
        cart.setGuest(guest);
        cart.setTotalAmount(BigDecimal.ZERO);

        Cart savedCart = cartRepository.save(cart);

        Cart freshCart = cartRepository.findWithCartItemsById(savedCart.getId())
                .orElse(savedCart);

        List<CartItemResponseDTO> items = freshCart.getCartItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        CartResponseDTO cartResponseDTO = new CartResponseDTO(
                freshCart.getId(),
                freshCart.getGuest().getId(),
                freshCart.getTotalAmount(),
                items
        );

        logger.info("✅ Корзина создана: ID {} для гостя {}", freshCart.getId(), guest.getId());
        return cartResponseDTO;
    }

    @Transactional
    public CartItemResponseDTO addCartItem(@Valid CartItemRequestDTO cartItemRequestDTO) {
        Cart cart = cartRepository.findWithCartItemsAndDishesById(cartItemRequestDTO.cartId())
                .orElseThrow(() -> new CommonException("Корзина с ID " + cartItemRequestDTO.cartId() + " не найдена"));

        Dish dish = dishRepository.findById(cartItemRequestDTO.dishId())
                .orElseThrow(() -> new CommonException("Блюдо с ID " + cartItemRequestDTO.dishId() + " не найдено"));

        if (cartItemRequestDTO.count() <= 0) {
            throw new CommonException("Количество блюда должно быть больше 0");
        }

        boolean dishAlreadyInCart = cart.getCartItems().stream()
                .anyMatch(item -> item.getDish().getId().equals(dish.getId()));
        if (dishAlreadyInCart) {
            throw new CommonException("Блюдо уже есть в корзине");
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setDish(dish);
        cartItem.setCount(cartItemRequestDTO.count());
        cartItem.setUnitPrice(dish.getCost());

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        cart.getCartItems().add(savedCartItem);
        logger.info("Элементов в корзине после добавления: {}", cart.getCartItems().size());
        cart.recalculateTotalAmount();
        logger.info("TotalAmount после пересчета: {}", cart.getTotalAmount());
        Cart savedCart = cartRepository.save(cart);
        logger.info("TotalAmount после сохранения: {}", savedCart.getTotalAmount());

        logger.info("Добавлен элемент корзины ID: {} в корзину ID: {}. Блюдо: {}, количество: {}",
                savedCartItem.getId(), cart.getId(), dish.getTitle(), cartItemRequestDTO.count());

        CartItemResponseDTO cartItemResponseDTO = mapToCartItemResponse(savedCartItem);

        return cartItemResponseDTO;
    }

    @Transactional
    public void dropCart(String phone) {
        Cart cart = cartRepository.findByGuestPhoneWithItems(phone)
                .orElseThrow(() -> new CommonException("Корзина не найдена"));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.delete(cart);

        logger.info("Корзина пользователя с номером {} удалена", phone);
    }

    public void dropCartItem(@Valid CartItemDeleteRequestDTO cartItemDeleteRequestDTO) {
        Cart cart = cartRepository.findByGuestPhoneWithItems(cartItemDeleteRequestDTO.phone())
                .orElseThrow(() -> new CommonException("Корзина не найдена"));
        CartItem cartItem = cartItemRepository.findById(cartItemDeleteRequestDTO.cartItemId())
                .orElseThrow(() -> new CommonException("Блюдо, которое хотите удалить не найдено"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CommonException("Элемент не принадлежит корзине пользователя");
        }

        cartItemRepository.delete(cartItem);
        cart.getCartItems().remove(cartItem);
        cart.recalculateTotalAmount();
        cartRepository.save(cart);

        logger.info("блюдо {} удалено", cartItem.getDish().getTitle());
    }

    @Override
    public CartItemResponseDTO updateQuantityCartItem(UpdateQuantityCartItemRequestDTO updateQuantityCartItemRequestDTO) {
        Cart cart = cartRepository.findByGuestPhoneWithItems(updateQuantityCartItemRequestDTO.phone())
                .orElseThrow(() -> new CommonException("Корзина не найдена"));
        logger.info(String.valueOf(updateQuantityCartItemRequestDTO.cartItemId()));
        CartItem cartItem = cartItemRepository.findById(updateQuantityCartItemRequestDTO.cartItemId())
                .orElseThrow(() -> new CommonException("Блюдо, которому хотите изменить количество не найдено"));

        if (!cartItem.getDish().isAvailable()){
            throw new CommonException("Блюда нет в наличии");
        }
        cartItem.setCount(updateQuantityCartItemRequestDTO.count());
        CartItem savedItem = cartItemRepository.save(cartItem);

        cart.recalculateTotalAmount();
        cartRepository.save(cart);
        return mapToCartItemResponse(savedItem);
    }

    private CartItemResponseDTO mapToCartItemResponse(CartItem cartItem) {
        return new CartItemResponseDTO(
                cartItem.getId(),
                cartItem.getDish().getId(),
                cartItem.getDish().getTitle(),
                cartItem.getCount(),
                cartItem.getUnitPrice(),
                cartItem.getTotalPrice()
        );
    }
}
