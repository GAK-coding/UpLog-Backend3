package com.uplog.uplog.domain.menu.application;

import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import com.uplog.uplog.domain.menu.exception.*;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.application.TaskService;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.exception.NotFoundTaskByIdException;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuRepository menuRepository;
    private final ProjectRepository projectRepository;
    private final PostRepository postRepository;
    private final TaskService taskService;
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
    public Menu createMenu(Long projectId, @RequestBody CreateMenuRequest createMenuRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundIdException::new);


        // 중복 검사
        String menuName = createMenuRequest.getMenuName();
        List<Menu> existingMenus = menuRepository.findByProjectIdAndMenuName(projectId, menuName);
        if (!existingMenus.isEmpty()) {
            throw new DuplicatedMenuNameInProjectException();
        }

        Menu menu = createMenuRequest.toEntity(project);
        menuRepository.save(menu);
        return menu;
    }

    /*
    DELETE
     */
    @Transactional
    public void deleteMenu(Long menuId){
        Menu menu=menuRepository.findById(menuId).orElseThrow(NotFoundIdException::new);
        if ("결과물".equals(menu.getMenuName())) {
            throw new MenuUpdateNotAllowedException();
        }
        menuRepository.delete(menu);
    }

    /*
    UPDATE
     */
    @Transactional
    public Menu updateMenuName(Long id,UpdateMenuNameRequest updateMenuNameRequest){
        Menu menu=menuRepository.findById(id).orElseThrow(NotFoundIdException::new);

        String updateName=updateMenuNameRequest.getUpdatemenuName();

        //결과물은 수정 안돼
        if ("결과물".equals(menu.getMenuName())) {
            throw new MenuUpdateNotAllowedException();
        }

        // 기존 메뉴 이름과 업데이트하고자 하는 이름이 같은 경우
        if (menu.getMenuName().equals(updateName)) {
            throw new DuplicatedMenuNameInProjectException("현재 메뉴이름과 동일합니다.");
        }

        // 중복 검사
        String menuName = updateMenuNameRequest.getUpdatemenuName();
        List<Menu> existingMenus = menuRepository.findByProjectIdAndMenuName(id, menuName);
        if (!existingMenus.isEmpty()) {
            throw new DuplicatedMenuNameInProjectException();
        }
        menu.updateMenuName(updateMenuNameRequest.getUpdatemenuName());
        return menu;
    }

    @Transactional
    public Menu updateNoticePost(Long menuId,UpdateNoticePostRequest updateNoticePostRequest){
        Menu menu = menuRepository.findById(menuId).orElseThrow(NotFoundIdException::new);
        Post post=postRepository.findById(updateNoticePostRequest.getUpdateNoticePostId()).orElseThrow(NotFoundTaskByIdException::new);
        menu.updateNoticePost(post);
        return menu;
    }
//    @Transactional(readOnly = true)
//    public List<TaskInfoDTO> findTasksByMenuId(Long menuId) {
//        List<TaskInfoDTO> taskList = taskService.findByMenuId(menuId);
//        return taskList.stream()
//                .map(TaskInfoDTO::toTask) // 이 부분 수정
//                .collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public List<MenuInfoDTO> findByProjectId(Long projectId){
        List<Menu> menuList=menuRepository.findByProjectId(projectId);
        List<MenuInfoDTO> menuInfoDTOS=new ArrayList<>();
        for(Menu menu:menuList){
            MenuInfoDTO menuInfoDTO=menu.toMenuInfoDTO();
            menuInfoDTOS.add(menuInfoDTO);
        }
        return menuInfoDTOS;
    }

}

