package com.example.Kiosk.domain.dto.response;


import com.example.Kiosk.domain.entity.Option;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OptionResponse {
    private Long optionId;
    private String optionName;
    private Integer price;

    public static OptionResponse from(Option option) {
        return OptionResponse.builder()
                .optionId(option.getOptionId())
                .optionName(option.getName())
                .price(option.getPrice())
                .build();
    }
}
