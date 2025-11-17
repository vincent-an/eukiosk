package com.example.Kiosk.domain.entity;

import com.example.Kiosk.domain.entity.eums.MenuCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Option")
@Getter
@Setter
@NoArgsConstructor
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Integer price;

    @Column(name = "is_common")
    private Boolean isCommon = false; // 기본값 false

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private MenuCategory category; // (공통 옵션이면 null)
}
