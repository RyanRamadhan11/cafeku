package com.enigma.java_cafetaria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "m_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "menu_code", unique = true, nullable = false, length = 100)
    private String menuCode;

    @Column(name = "menu_name", nullable = false, length = 100)
    private String menuName;

    @Column(name = "status", nullable = false, length = 100)
    private String status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MenuPrice> menuPrices;

}
