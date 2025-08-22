package com.example.CafeManagerCore.service;

import com.example.CafeManagerCore.DTO.DishRequestDTO;
import com.example.CafeManagerCore.DTO.DishResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DishService {
    public List<DishResponseDTO> getAllDish();
    public ResponseEntity<DishResponseDTO> addDish(DishRequestDTO dishRequestDTO);
}
