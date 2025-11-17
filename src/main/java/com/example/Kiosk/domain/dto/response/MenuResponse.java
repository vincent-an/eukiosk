package com.example.Kiosk.domain.dto.response;

import com.example.Kiosk.domain.entity.Menu;
import com.example.Kiosk.domain.entity.eums.MenuCategory;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MenuResponse {
    private Long menuId;
    private String menuName;
    private Integer price;
    private MenuCategory category;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getMenuId())
                .menuName(menu.getName())
                .price(menu.getPrice())
                .category(menu.getCategory())
                .build();
    }
}
