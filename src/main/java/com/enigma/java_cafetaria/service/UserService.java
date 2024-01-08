package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.entity.AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    AppUser loadUserByUserId(String id);
}