package com.uplog.uplog.domain.menu.dao;

import com.uplog.uplog.domain.menu.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findById(Long id);
    List<Menu> findByProjectIdAndMenuName(Long projectId, String menuName);
    List<Menu> findByProjectId(Long projectId);
    long countByProjectId(Long projectId);

}
