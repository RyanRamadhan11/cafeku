package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.dto.requets.MenuRequest;
import com.enigma.java_cafetaria.dto.response.MenuResponse;
import com.enigma.java_cafetaria.entity.Menu;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MenuService {

    Menu create(Menu menu);

    List<Menu> getAll();

    Menu getById(String id);

    Menu update(Menu menu);

    void delete(String id);

    MenuResponse createMenuAndMenuPrice(MenuRequest menuRequest);

    MenuResponse updateMenuAndMenuPrice(MenuRequest menuRequest);

    MenuResponse getMenuAndMenuPriceByCategoryId(String id);

    void deleteMenuAndMenuPrice(String id);

    Page<MenuResponse> getAllMenuByCodeNamePrice(String productCode, String productName, Long minPrice, Long maxPrice, Integer page, Integer size);
}
