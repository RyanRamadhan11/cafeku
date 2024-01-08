package com.enigma.java_cafetaria.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MenuResponse {
    private String menuId;
    private String menuPriceId;
    private String menuCode;
    private String menuName;
    private String status;
    private Long price;
    private CategoryResponse category;

    public MenuResponse(List<MenuResponse> menuResponses) {

    }

}
