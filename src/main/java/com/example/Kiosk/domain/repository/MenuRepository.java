package com.example.Kiosk.domain.repository;

import com.example.Kiosk.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu,Long> {
}
