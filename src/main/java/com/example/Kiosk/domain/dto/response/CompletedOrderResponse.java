package com.example.Kiosk.domain.dto.response;

import com.example.Kiosk.domain.entity.Orders;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletedOrderResponse {

    private Long orderId;
    private Integer orderNumber;
    private Integer totalPrice;
    private LocalDateTime orderTime;

    private List<CartItemResponse> items;

    public static CompletedOrderResponse from(Orders orders) {
        List<CartItemResponse> itemResponses = orders.getOrderItems().stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());

        return CompletedOrderResponse.builder()
                .orderId(orders.getOrderId())
                .orderNumber(orders.getOrderNumber())
                .totalPrice(orders.getTotalPrice())
                .orderTime(orders.getOrderTime()) // (Orders 엔티티에 orderTime 필드 필요)
                .items(itemResponses)
                .build();
    }
}
