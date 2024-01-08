package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.dto.requets.CategoryRequest;
import com.enigma.java_cafetaria.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAll();

    CategoryResponse create(CategoryRequest categoryRequest);

    CategoryResponse update(CategoryRequest categoryRequest);

    void delete(String id);

    CategoryResponse getById(String id);
}
