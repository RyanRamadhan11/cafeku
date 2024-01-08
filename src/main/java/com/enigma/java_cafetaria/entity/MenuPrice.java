package com.enigma.java_cafetaria.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "m_menu_price")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MenuPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "price", columnDefinition = "bigint check(price>0)")
    private Long price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    @JsonBackReference
    private Menu menu;
}