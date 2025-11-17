package com.example.Kiosk.domain.controller;

import com.example.Kiosk.domain.dto.response.MenuResponse;
import com.example.Kiosk.domain.dto.response.OptionResponse;
import com.example.Kiosk.domain.entity.Menu;
import com.example.Kiosk.domain.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    //전체 메뉴 조회
    @GetMapping
    public ResponseEntity<List<MenuResponse>> getAllMenus() {
        List<MenuResponse> menus = menuService.getAllMenus();

        log.info("전체 메뉴 조회 완료 - {} 개 메뉴",  menus.size());
        return ResponseEntity.ok(menus);
    }

    // 토핑 추가 페이지 이동
    @GetMapping("/{menuId}/options")
    public ResponseEntity<List<OptionResponse>> getOptionsForMenu(@PathVariable Long menuId) {
        List<OptionResponse> options = menuService.getOptionsForMenu(menuId);

        log.info("메뉴(ID:{}) 옵션 조회 완료 - {} 개 옵션", menuId, options.size());
        return ResponseEntity.ok(options);
    }
}
