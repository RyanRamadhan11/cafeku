package com.enigma.java_cafetaria.repository;

import com.enigma.java_cafetaria.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {

    Optional<UserCredential> findByUsername(String username);
}
