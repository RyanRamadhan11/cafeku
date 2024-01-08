package com.enigma.java_cafetaria.dto.requets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoryRequest {

    private String id;

    private String categoryCode;

    private String categoryName;
}
