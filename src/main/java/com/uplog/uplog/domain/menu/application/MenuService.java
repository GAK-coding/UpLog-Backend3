package com.uplog.uplog.domain.menu.application;

import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuRepository menuRepository;
    private final ProjectRepository projectRepository;

    /*
    CREATE
     */


    @Transactional
    public List<Menu> createDefaultMenu(Long projectId){
        List<String> menuNames = Arrays.asList("결과물", "요구사항", "개발", "배포");
        List<Menu> createdMenus = new ArrayList<>();
        Project project=projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        for (String menuName : menuNames) {
            Menu menu = Menu.builder()
                    .menuName(menuName)
                    .project(project)
                    .build();
            createdMenus.add(menu);
        }

        menuRepository.saveAll(createdMenus);
        return createdMenus;
    }
    @Transactional
    public Menu createMenu(Long projectId, @RequestBody CreateMenuRequest createMenuRequest){
        Project project=projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Menu menu=createMenuRequest.toEntity(project);
        menuRepository.save(menu);
        return menu;
    }
}

