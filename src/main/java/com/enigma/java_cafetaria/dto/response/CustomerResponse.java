package com.enigma.java_cafetaria.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CustomerResponse {
    private String id;
    private String customerName;
    private String address;
    private String phone;
    private String email;
}


