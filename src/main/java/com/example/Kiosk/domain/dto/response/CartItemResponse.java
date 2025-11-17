package com.example.Kiosk.domain.dto.response;

import com.example.Kiosk.domain.entity.OrderItem;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CartItemResponse {

    private Long orderItemId;
    private String menuName;
    private Integer quantity;
    private Integer itemTotalPrice; // (옵션 포함된 개별 항목 총액)

    public static CartItemResponse from(OrderItem orderItem) {
        return CartItemResponse.builder()
                .orderItemId(orderItem.getOrderItemId())
                .menuName(orderItem.getMenu().getName()) // (연관 엔티티 조회)
                .quantity(orderItem.getQuantity())
                .itemTotalPrice(orderItem.getItemTotalPrice())
                .build();
    }
}
