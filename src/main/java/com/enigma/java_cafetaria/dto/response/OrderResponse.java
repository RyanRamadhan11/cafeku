package com.enigma.java_cafetaria.dto.response;

import com.enigma.java_cafetaria.constant.EOrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OrderResponse {
    private String billId;
    private String receiptNumber;
    private LocalDateTime transDate;
    private EOrderType orderType;
    private List<OrderDetailResponse> orderDetailResponses;
}
