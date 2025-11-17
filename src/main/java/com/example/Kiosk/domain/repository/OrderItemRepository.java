package com.example.Kiosk.domain.repository;

import com.example.Kiosk.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

}
