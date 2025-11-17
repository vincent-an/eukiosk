package com.example.Kiosk.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "OrderItemOption")
@Getter
@Setter
@NoArgsConstructor
public class OrderItemOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ItemOption_id")
    private Long itemOptionId;

    @Column(name = "quantity")
    private Integer quantity;

    // 'OrderItemOption' (N) : 'OrderItem' (1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderItem_id") // DDL에 정의된 외래 키(FK) 컬럼
    private OrderItem orderItem;

    // 'OrderItemOption' (N) : 'Option' (1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id") // DDL에 정의된 외래 키(FK) 컬럼
    private Option option;
}
