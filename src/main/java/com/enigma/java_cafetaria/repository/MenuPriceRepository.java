package com.enigma.java_cafetaria.repository;

import com.enigma.java_cafetaria.entity.MenuPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuPriceRepository extends JpaRepository<MenuPrice, String> {
//    Optional<ProductPrice> findByProduct_IdAndIsActive(String productId, Boolean active);
}