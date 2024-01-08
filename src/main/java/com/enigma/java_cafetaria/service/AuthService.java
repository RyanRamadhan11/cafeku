package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.dto.requets.AuthRequest;
import com.enigma.java_cafetaria.dto.response.LoginResponse;
import com.enigma.java_cafetaria.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse registerCustomer(AuthRequest request);

    RegisterResponse registerAdmin(AuthRequest request);

    LoginResponse login(AuthRequest authRequest);
}