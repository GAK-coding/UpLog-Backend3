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
    public Menu createMenu(Long projectId, @RequestBody CreateMenuRequest createMenuRequest){
        Project project=projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Menu menu=createMenuRequest.toEntity(project);
        menuRepository.save(menu);
        return menu;
    }
}

