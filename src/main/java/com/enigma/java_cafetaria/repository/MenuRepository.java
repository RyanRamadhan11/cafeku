package com.enigma.java_cafetaria.repository;

import com.enigma.java_cafetaria.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String>, JpaSpecificationExecutor<Menu> {
    List<Menu> findByMenuPrices_Category_Id(String categoryId);
}
