package com.enigma.java_cafetaria.controller;

import com.enigma.java_cafetaria.constant.AppPath;
import com.enigma.java_cafetaria.dto.requets.MenuRequest;
import com.enigma.java_cafetaria.dto.response.CommonResponse;
import com.enigma.java_cafetaria.dto.response.MenuResponse;
import com.enigma.java_cafetaria.dto.response.PagingResponse;
import com.enigma.java_cafetaria.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppPath.MENU)
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @PostMapping
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createMenu(@RequestBody MenuRequest menuRequest){
        MenuResponse menuResponse = menuService.createMenuAndMenuPrice(menuRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<MenuResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully created new menu")
                        .data(menuResponse)
                        .build());
    }

    @PutMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SELLER')")
    public ResponseEntity<?> updateMenu(@RequestBody MenuRequest menuRequest){
        MenuResponse menuResponse = menuService.updateMenuAndMenuPrice(menuRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<MenuResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully update menu")
                        .data(menuResponse)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuByCategoryId(@PathVariable String id) {
        MenuResponse menuResponse = menuService.getMenuAndMenuPriceByCategoryId(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<MenuResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get menu by category id")
                        .data(menuResponse)
                        .build());

    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuAndMenuPrice(@PathVariable String id) {
        menuService.deleteMenuAndMenuPrice(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully Delete menu and menu Price")
                        .data(HttpStatus.OK)
                        .build());
    }

    @GetMapping
    public ResponseEntity<?> getAllProductPage(
            @RequestParam(name = "menuCode", required = false) String menuCode,
            @RequestParam(name = "menuName", required = false) String menuName,
            @RequestParam(name = "minPrice", required = false) Long minPrice,
            @RequestParam(name = "maxPrice", required = false) Long maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "5") Integer size
    ){

        Page<MenuResponse> menuResponses = menuService.getAllMenuByCodeNamePrice(menuCode, menuName, minPrice, maxPrice, page, size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(menuResponses.getTotalPages())
                .size(size)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully get all menus")
                        .data(menuResponses.getContent())
                        .paging(pagingResponse)
                        .build());
    }
}
