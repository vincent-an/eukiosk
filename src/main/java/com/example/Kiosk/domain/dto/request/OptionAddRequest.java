package com.example.Kiosk.domain.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OptionAddRequest { // 내부에 포함된느 걸로, 어떤 옵션을 몇 개 추가했는지 정보가 담김

    // 옵션 종류
    private Long optionId;

    // 해당 옵션으로 몇 개 선택했는지
    private Integer quantity;
}
