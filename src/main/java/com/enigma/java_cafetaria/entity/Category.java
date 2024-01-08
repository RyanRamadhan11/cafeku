package com.enigma.java_cafetaria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "m_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "category_code", unique = true, nullable = false, length = 100)
    private String categoryCode;

    @Column(name = "category_name", nullable = false, length = 200)
    private String categoryName;

}
