package com.example.Kiosk.domain.dto.response;

import com.example.Kiosk.domain.entity.OrderItem;
import com.example.Kiosk.domain.entity.Orders;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CartResponse { // 전체 장바구니 DTO

    private Long orderId;
    private Integer totalPrice;
    private Integer totalQuantity; // (계산된 전체 수량)
    private List<CartItemResponse> items;

    public static CartResponse from(Orders orders) {

        // 전체 개수 변수가 없으므로 직접 계산
        int totalQuantity = orders.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        List<CartItemResponse> itemResponses = orders.getOrderItems().stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());

        return CartResponse.builder()
                .orderId(orders.getOrderId())
                .totalPrice(orders.getTotalPrice())
                .totalQuantity(totalQuantity)
                .items(itemResponses)
                .build();
    }
}
