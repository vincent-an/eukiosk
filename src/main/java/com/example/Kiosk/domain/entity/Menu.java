package com.example.Kiosk.domain.entity;

import com.example.Kiosk.domain.entity.eums.MenuCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Integer price; // int 대신 Integer 사용 권장 (Null 처리)

    @Enumerated(EnumType.STRING) // (이게 제일 중요!)
    @Column(name = "category")
    private MenuCategory category;
}
