package com.example.CafeManagerCore.service.impl;

import com.example.CafeManagerCore.DTO.DishRequestDTO;
import com.example.CafeManagerCore.DTO.DishResponseDTO;
import com.example.CafeManagerCore.controller.DishController;
import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.model.Dish;
import com.example.CafeManagerCore.model.DishCategory;
import com.example.CafeManagerCore.repository.DishCategoryRepository;
import com.example.CafeManagerCore.repository.DishRepository;
import com.example.CafeManagerCore.service.DishService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DishServiceImpl implements DishService {
    public final DishRepository dishRepository;
    private final DishCategoryRepository dishCategoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(DishController.class);

    @Override
    public List<DishResponseDTO> getAllDish() {
        return dishRepository.findAll().stream().map(a -> new DishResponseDTO(
                a.getId(),
                a.getTitle(),
                a.getDescription(),
                a.getCost(),
                a.getRating(),
                a.getDiscount(),
                a.getDishCategory().getTitle()
        )).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ResponseEntity<DishResponseDTO> addDish(@Valid DishRequestDTO dishRequestDTO) {
        try {
            Dish newDish = new Dish();
            newDish.setTitle(dishRequestDTO.title());
            newDish.setDescription(dishRequestDTO.description());
            newDish.setCost(dishRequestDTO.cost());
            DishCategory dishCategory = dishCategoryRepository.findById(dishRequestDTO.dishCategory()).orElseThrow(() -> new CommonException("Категория не найдена"));
            newDish.setDishCategory(dishCategory);

            dishRepository.save(newDish);

            return ResponseEntity.ok(mapDishToResponseDTO(newDish));
        } catch (CommonException e) {
        logger.error("Ошибка при создании блюда: {}", e.getMessage());
        throw new CommonException("Ошибка при создании блюда: " + e.getMessage());
    } catch (Exception e) {
        logger.error("Необработанная ошибка при создании блюда: ", e);
        throw new CommonException("Ошибка при создании блюда: " + e.getMessage());
    }
    }

    public DishResponseDTO mapDishToResponseDTO(Dish dish){
        return new DishResponseDTO(
                dish.getId(),
                dish.getTitle(),
                dish.getDescription(),
                dish.getCost(),
                dish.getRating(),
                dish.getDiscount(),
                dish.getDishCategory().getTitle()
        );
    }
}
