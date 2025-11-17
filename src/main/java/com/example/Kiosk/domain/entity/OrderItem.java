package com.example.Kiosk.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "OrderItem")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderItem_id")
    private Long orderItemId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "itemTotalPrice")
    private Integer itemTotalPrice;

    // 'OrderItem' (N) : 'Orders' (1)
    // FetchType.LAZY : 성능 최적화를 위해 실제 Orders가 필요할 때만 DB에서 조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // DDL에 정의된 외래 키(FK) 컬럼
    private Orders orders;

    // 'OrderItem' (N) : 'Menu' (1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id") // DDL에 정의된 외래 키(FK) 컬럼
    private Menu menu;

    // 'OrderItem' (1) : 'OrderItemOption' (N)
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<OrderItemOption> orderItemOptions = new ArrayList<>();
}
