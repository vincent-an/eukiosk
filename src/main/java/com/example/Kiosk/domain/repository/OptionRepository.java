package com.example.Kiosk.domain.repository;

import com.example.Kiosk.domain.entity.Option;
import com.example.Kiosk.domain.entity.eums.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option,Long> {

    List<Option> findByIsCommonTrueOrCategory(MenuCategory category);
}
