package com.uplog.uplog.domain.menu.api;

import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.dto.PostDTO;
import com.uplog.uplog.domain.task.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    DELETE
     */
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id){
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    /*
    PATCH
     */
    @PatchMapping("/menus/{menu-id}/menuname")
    public ResponseEntity<MenuInfoDTO> updateMenuName(@PathVariable Long id, @RequestBody UpdateMenuNameRequest updateMenuNameRequest){
        Menu updatedMenuName=menuService.updateMenuName(id,updateMenuNameRequest);
        MenuInfoDTO menuInfoDTO=updatedMenuName.toMenuInfoDTO();
        return ResponseEntity.ok(menuInfoDTO);
    }

    @PatchMapping("/menus/{menu-id}/notice-post")
    public ResponseEntity<MenuInfoDTO> updateNotiePost(@PathVariable Long menuId,@RequestBody UpdateNoticePostRequest updateNoticePostRequest){
        Menu updatedNoticePost=menuService.updateNoticePost(menuId,updateNoticePostRequest);
        MenuInfoDTO menuInfoDTO=updatedNoticePost.toMenuInfoDTO();
        return ResponseEntity.ok(menuInfoDTO);
    }


    /*
    READ
     */
    @GetMapping("/menus/{project-id}")
    public ResponseEntity<List<MenuInfoDTO>> findMenuByPost(@PathVariable Long proijectId){
        List<MenuInfoDTO> menuInfoDTOs=menuService.findByProjectId(proijectId);
        return new ResponseEntity<>(menuInfoDTOs, HttpStatus.OK);
    }

    //메뉴별 테스크 가져오기->근데 이걸 메뉴에서 처리하는게 맞을까(==포스트도 )
    @GetMapping("/menus/{menu-id}/tasks")
    public ResponseEntity<MenuTasksDTO> findTasksAndMenuInfoByMenuId(@PathVariable("menu-id") Long menuId) {
        List<TaskDTO.TaskInfoDTO> taskInfoDTOList = menuService.findTasksByMenuId(menuId);
        MenuInfoDTO menuInfoDTO = menuService.findMenuById(menuId);

        MenuTasksDTO menuTasksDTO = new MenuTasksDTO(menuInfoDTO, taskInfoDTOList);
        return ResponseEntity.ok(menuTasksDTO);
    }
//    @GetMapping("/menus/{menu-id}/tasks")
//    public ResponseEntity<List<TaskDTO.TaskInfoDTO>> findTasksByMenuId(@PathVariable("menu-id") Long menuId) {
//        List<TaskDTO.TaskInfoDTO> taskInfoDTOList = menuService.findTasksByMenuId(menuId);
//        return ResponseEntity.ok(taskInfoDTOList);
//    }

    //메뉴별 포스트 가져오기
    @GetMapping("/menus/{menu-id}/posts")
    public ResponseEntity<MenuPostsDTO> findMenuPosts(@PathVariable("menu-id") Long menuId) {
        MenuPostsDTO menuPostsDTO = menuService.findMenuInfoById(menuId);
        return ResponseEntity.ok(menuPostsDTO);
    }
//    @GetMapping("/menus/{menu-id}/posts")
//    public ResponseEntity<List<PostDTO.PostInfoDTO>> findPostsByMenuId(@PathVariable("menu-id") Long menuId) {
//        List<PostDTO.PostInfoDTO> postInfoDTOList = menuService.findPostsByMenuId(menuId);
//        return ResponseEntity.ok(postInfoDTOList);
//    }





}
