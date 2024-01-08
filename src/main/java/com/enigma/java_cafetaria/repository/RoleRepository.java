package com.enigma.java_cafetaria.repository;

import com.enigma.java_cafetaria.constant.ERole;
import com.enigma.java_cafetaria.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
