package com.example.Kiosk.domain.service;

import com.example.Kiosk.domain.dto.response.MenuResponse;
import com.example.Kiosk.domain.dto.response.OptionResponse;
import com.example.Kiosk.domain.entity.Menu;
import com.example.Kiosk.domain.entity.Option;
import com.example.Kiosk.domain.entity.eums.MenuCategory;
import com.example.Kiosk.domain.repository.MenuRepository;
import com.example.Kiosk.domain.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final OptionRepository optionRepository;

    @Transactional(readOnly = true)
    public List<MenuResponse> getAllMenus() {
        log.info("전체 메뉴판 조회 요청");

        // 전체 메뉴 조회 기능
        List<Menu> menus = menuRepository.findAll();

        return menus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }

    public List<OptionResponse> getOptionsForMenu(Long menuId) {
        log.info("항목별 옵션 조회 요청 (Menu ID: {})", menuId);

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메뉴를 찾을 수 없습니다. : " + menuId));

        MenuCategory category = menu.getCategory();

        List<Option> options = optionRepository.findByIsCommonTrueOrCategory(category);

        return options.stream()
                .map(OptionResponse::from)
                .collect(Collectors.toList());
    }
}
