package com.enigma.java_cafetaria.entity;

import com.enigma.java_cafetaria.constant.EOrderType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "t_order")
//untuk geter setter
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "order_type", nullable = false)
    private EOrderType orderType;

    @Column(name = "trans_date", nullable = false)
    private LocalDateTime transDate;

    @Column(name = "receipt_number", nullable = false)
    private String receiptNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderDetail> orderDetails;
}
