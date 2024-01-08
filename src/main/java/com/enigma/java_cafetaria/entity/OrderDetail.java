package com.enigma.java_cafetaria.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_order_detail")
//untuk geter setter
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_price_id")
    private MenuPrice menuPrice;

    @Column(name = "quantity")
    private Integer quantity;
}
