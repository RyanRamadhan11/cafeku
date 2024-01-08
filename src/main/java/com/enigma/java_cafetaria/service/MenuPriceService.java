package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.entity.MenuPrice;

public interface MenuPriceService {
    MenuPrice create(MenuPrice menuPrice);

    MenuPrice createOrUpdate(MenuPrice menuPrice);

    void delete(String id);

    MenuPrice getById(String id);

}