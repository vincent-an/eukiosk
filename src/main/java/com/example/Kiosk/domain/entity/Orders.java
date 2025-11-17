package com.example.Kiosk.domain.entity;

import com.example.Kiosk.domain.entity.eums.OrderStatus;
import com.example.Kiosk.domain.entity.eums.OrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(name = "totalPrice")
    private Integer totalPrice;

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 저장
    @Column(name = "ordertype")
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "orderTime")
    private LocalDateTime orderTime;

    // 'Orders' 1개는 여러 'OrderItem'을 가짐
    // 'mappedBy = "orders"' : OrderItem 클래스에 있는 'orders' 필드가 주인(FK)임을 명시
    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
}
