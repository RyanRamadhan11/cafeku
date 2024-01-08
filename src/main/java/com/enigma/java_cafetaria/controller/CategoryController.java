package com.enigma.java_cafetaria.controller;

import com.enigma.java_cafetaria.constant.AppPath;
import com.enigma.java_cafetaria.dto.requets.CategoryRequest;
import com.enigma.java_cafetaria.dto.response.CategoryResponse;
import com.enigma.java_cafetaria.dto.response.CommonResponse;
import com.enigma.java_cafetaria.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(AppPath.CATEGORY)
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        List<CategoryResponse> categoryList = categoryService.getAll();

        return ResponseEntity.ok(
                CommonResponse.<List<CategoryResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully retrieved all categories")
                        .data(categoryList)
                        .build());
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.create(categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<CategoryResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully created new category")
                        .data(categoryResponse)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable String id) {
        CategoryResponse categoryResponse = categoryService.getById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<CategoryResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get category by id")
                        .data(categoryResponse)
                        .build());

    }

    @PutMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SELLER')")
    public ResponseEntity<?> updateCategory(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.update(categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<CategoryResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully update category")
                        .data(categoryResponse)
                        .build());
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable String id) {
        categoryService.delete(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully Delete Category")
                        .data(HttpStatus.OK)
                        .build());
    }
}
