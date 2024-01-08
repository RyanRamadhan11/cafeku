package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.dto.requets.OrderRequest;
import com.enigma.java_cafetaria.dto.response.OrderResponse;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderResponse createNewOrder(OrderRequest orderRequest);
    OrderResponse getOrderById(String id);
    List<OrderResponse> getAllOrder();

}
