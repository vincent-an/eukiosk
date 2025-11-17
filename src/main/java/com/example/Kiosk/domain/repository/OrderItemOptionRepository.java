package com.example.Kiosk.domain.repository;

import com.example.Kiosk.domain.entity.OrderItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemOptionRepository extends JpaRepository<OrderItemOption,Long> {
}
