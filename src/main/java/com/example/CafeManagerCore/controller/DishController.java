package com.example.CafeManagerCore.controller;

import com.example.CafeManagerCore.DTO.DishRequestDTO;
import com.example.CafeManagerCore.DTO.DishResponseDTO;
import com.example.CafeManagerCore.exception.CommonException;
import com.example.CafeManagerCore.exception.ErrorResponse;
import com.example.CafeManagerCore.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishController {
    private final DishService dishService;
    private static final Logger logger = LoggerFactory.getLogger(DishController.class);

    @GetMapping
    public ResponseEntity<List<DishResponseDTO>> getAllDish(){
        List<DishResponseDTO> dishList =  dishService.getAllDish();
        try {
            if (dishList.isEmpty()) {
                logger.info("List dishes empty");
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(dishList);
        } catch (Exception e){
            logger.info("Error when getting the dishes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> addDish(@RequestBody @Valid DishRequestDTO dishRequestDTO){
        try {
            logger.info("Начало создания блюда в контроллере");
            ResponseEntity<DishResponseDTO> dtoResponseEntity = dishService.addDish(dishRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoResponseEntity);
        } catch (CommonException e) {
            logger.error("Произошла ошибка при получении блюда", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse("Ошибка при создании блюда: " + e.getMessage());
            logger.error("Произошла ошибка при получении блюда", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
