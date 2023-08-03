package com.uplog.uplog.domain.menu.api;

import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.model.Menu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MenuController {
    private final MenuService menuService;

    /*
    CREATE
     */
    @PostMapping("/menus/{project-id}/default")
    public ResponseEntity<List<MenuInfoDTO>> createDefaultMenus(@PathVariable(name="project-id") Long projectId) {
        List<Menu> createdMenus = menuService.createDefaultMenu(projectId);
        List<MenuInfoDTO> menuInfoDTOList = createdMenus.stream()
                .map(Menu::toMenuInfoDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(menuInfoDTOList);
    }

    @PostMapping(value="/menus/{project-id}")
    public ResponseEntity<MenuInfoDTO> createMenu(@PathVariable(name="project-id") Long projectId, @RequestBody CreateMenuRequest createMenuRequest){
        Menu createMenu=menuService.createMenu(projectId,createMenuRequest);
        MenuInfoDTO menuInfoDTO=createMenu.toMenuInfoDTO();
        return ResponseEntity.ok(menuInfoDTO);
    }

    /*
    READ
     */

}
