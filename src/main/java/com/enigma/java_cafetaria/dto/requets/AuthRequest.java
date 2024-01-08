package com.enigma.java_cafetaria.dto.requets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AuthRequest {
    private String username;
    private String password;
    private String customerName;
    private String address;
    private String mobilePhone;
    private String email;
}

