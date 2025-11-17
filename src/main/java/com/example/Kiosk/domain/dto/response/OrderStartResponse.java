package com.example.Kiosk.domain.dto.response;

import com.example.Kiosk.domain.entity.Orders;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderStartResponse {

    private Long orderId;
    private String status; // (e.g., "PENDING")

    public static OrderStartResponse from(Orders orders) {
        return OrderStartResponse.builder()
                .orderId(orders.getOrderId())
                .status(orders.getStatus().name()) // (Enum -> String)
                .build();
    }
}
