package com.enigma.java_cafetaria.dto.requets;

import com.enigma.java_cafetaria.constant.EOrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OrderRequest {
    private EOrderType orderType;
    private List<OrderDetailRequest> billDetails;
}
