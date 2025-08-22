package com.example.CafeManagerCore.repository;

import com.example.CafeManagerCore.model.DishCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishCategoryRepository extends JpaRepository<DishCategory, Long> {

}
