package com.example.Kiosk.domain.dto.request;


import com.example.Kiosk.domain.dto.response.OptionResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CartItemAddRequest { // 전체 요청을 처리하는 DTO

    // 메뉴 판별
    private Long menuId;

    // 동일한 옵션으로 몇 개 주문 했는지
    private Integer quantity;

    // 어떤 옵션을 선택했는지
    private List<OptionAddRequest> options;
}
