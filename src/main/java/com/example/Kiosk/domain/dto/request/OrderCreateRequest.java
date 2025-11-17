package com.example.Kiosk.domain.dto.request;

import com.example.Kiosk.domain.entity.eums.OrderType;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderCreateRequest {

    @NotNull(message = "주문 유형(매장/포장)은 필수입니다.")
    private OrderType orderType; // "EAT_IN" 또는 "TAKE_OUT"
}
