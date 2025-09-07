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
import com.example.CafeManagerCore.service.ImageUploadService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DishServiceImpl implements DishService {
    public final DishRepository dishRepository;
    private final DishCategoryRepository dishCategoryRepository;
    private final ImageUploadService imageUploadService;
    private static final Logger logger = LoggerFactory.getLogger(DishController.class);

    @Override
    public List<DishResponseDTO> getAllDish() {
        return dishRepository.findAll().stream()
                .map(this::mapDishToResponseDTOWithImage)
                .collect(Collectors.toList());
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

            byte[] imageBytes = Base64.getDecoder().decode(dishRequestDTO.imageBase64());
            String imageUrl = imageUploadService.uploadImageBytes(imageBytes, "dishes", UUID.randomUUID().toString());
            logger.error(imageUrl);
            newDish.setImageUrl(imageUrl);

            dishRepository.save(newDish);

            return ResponseEntity.ok(mapDishToResponseDTOWithImage(newDish));
        } catch (CommonException e) {
            logger.error("Ошибка при создании блюда: {}", e.getMessage());
            throw new CommonException("Ошибка при создании блюда: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Необработанная ошибка при создании блюда: ", e);
            throw new CommonException("Ошибка при создании блюда: " + e.getMessage());
        }
    }

    private DishResponseDTO mapDishToResponseDTOWithImage(Dish dish) {
        String imageBase64 = null;
        try {
            if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
                byte[] imageBytes = imageUploadService.downloadImage(dish.getImageUrl());
                imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
            }
        } catch (Exception e) {
            logger.warn("Не удалось загрузить изображение для блюда {}: {}", dish.getId(), e.getMessage());
        }

        return new DishResponseDTO(
                dish.getId(),
                dish.getTitle(),
                dish.getDescription(),
                dish.getCost(),
                dish.getRating(),
                dish.getDiscount(),
                dish.getDishCategory().getTitle(),
                imageBase64
        );
    }
}
