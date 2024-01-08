package com.enigma.java_cafetaria.dto.requets;

import com.enigma.java_cafetaria.dto.response.CategoryResponse;
import com.enigma.java_cafetaria.entity.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MenuRequest {
    private String id;

    @NotBlank(message = "menu code is required")
    private String menuCode;

    @NotBlank(message = "menu name is required")
    private String menuName;

    @NotBlank(message = "menu price is required")
    @Min(value = 0,message = "menu price must be greater than 0")
    private Long price;

    @NotBlank(message = "status is required")
    private String status;

    @NotBlank(message = "categoryId is required")
    private CategoryResponse categoryId;
}

