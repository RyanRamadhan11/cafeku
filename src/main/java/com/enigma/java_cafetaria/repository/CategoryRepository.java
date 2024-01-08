package com.enigma.java_cafetaria.repository;

import com.enigma.java_cafetaria.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Category findByCategoryCode (String categoryCode);

}
