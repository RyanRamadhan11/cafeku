package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.entity.Role;

public interface RoleService {
    Role getOrSave(Role role);
}
