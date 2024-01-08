package com.enigma.java_cafetaria.dto.response;

import com.enigma.java_cafetaria.entity.UserCredential;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdminResponse{
    private String id;

    private String name;

    private String email;

    private String phoneNumber;

    private UserCredential userCredential;
}
