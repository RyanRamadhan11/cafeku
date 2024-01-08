package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.dto.response.AdminResponse;
import com.enigma.java_cafetaria.dto.response.CustomerResponse;

import java.util.List;

public interface AdminService {
    List<AdminResponse> getAll();
}
