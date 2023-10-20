package com.uplog.uplog.domain.menu.api;

import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.task.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MenuController {
    private final MenuService menuService;

    /*
    CREATE
     */
    @PostMapping("/menus/{project-id}/default")
    public ResponseEntity<String> createDefaultMenus(@PathVariable(name="project-id") Long projectId) {
        List<SimpleMenuInfoDTO> createdMenus = menuService.createDefaultMenu(projectId);
        return ResponseEntity.ok("success");
    }

    @PostMapping(value="/menus/{project-id}")
    public ResponseEntity<Long> createMenu(@PathVariable(name="project-id") Long projectId, @RequestBody CreateMenuRequest createMenuRequest){
        Long menuInfoDTO=menuService.createMenu(projectId,createMenuRequest);
        return ResponseEntity.ok(menuInfoDTO);
    }


    /*
    DELETE
     */// 삭제시 밑에꺼 다 삭제되는거
    @DeleteMapping("/menus/{menu-id}")
    public ResponseEntity<String> deleteMenu(@PathVariable(name="menu-id") Long id){
        String m=menuService.deleteMenu(id);
        return ResponseEntity.ok(m);
    }

    /*
    PATCH
     */
    @PatchMapping("/menus/{menu-id}/menuname")
    public ResponseEntity<Long> updateMenuName(@PathVariable(name="menu-id") Long id, @RequestBody UpdateMenuNameRequest updateMenuNameRequest){
        Long menuInfoDTO=menuService.updateMenuName(id,updateMenuNameRequest);

        return ResponseEntity.ok(menuInfoDTO);
    }

    @PatchMapping("/menus/{menu-id}/notice-post")
    public ResponseEntity<Long> updateNoticePost(@PathVariable(name="menu-id") Long menuId, @RequestBody UpdateNoticePostRequest updateNoticePostRequest){
        Long menuInfoDTO=menuService.updateNoticePost(menuId,updateNoticePostRequest);
        return ResponseEntity.ok(menuInfoDTO);
    }

    @DeleteMapping("/menus/{menu-id}/reset-notice")
    public ResponseEntity<String> deleteNoticePost(@PathVariable(name="menu-id")  Long menuId) {
        String result = menuService.deleteNoticePost(menuId);
        return ResponseEntity.ok(result);
    }


    /*
    READ
     */
    @GetMapping("/menus/{project-id}")
    public ResponseEntity<List<MenuInfoDTO>> findMenuByPost(@PathVariable(name="project-id") Long projectId){
        List<MenuInfoDTO> menuInfoDTOs=menuService.findByProjectId(projectId);
        return new ResponseEntity<>(menuInfoDTOs, HttpStatus.OK);
    }

    //메뉴별 테스크 가져오기->근데 이걸 메뉴에서 처리하는게 맞을까(==포스트도 )
    @GetMapping("/menus/{menu-id}/tasks")
    public ResponseEntity<List<TaskDTO.TaskInfoDTO>> findTasksAndMenuInfoByMenuId(@PathVariable(name="menu-id") Long menuId) {
        List<TaskDTO.TaskInfoDTO> taskInfoDTOList = menuService.findTasksByMenuId(menuId);
        //MenuInfoDTO menuInfoDTO = menuService.findMenuById(menuId);

        //MenuTasksDTO menuTasksDTO = new MenuTasksDTO(menuInfoDTO, taskInfoDTOList);
        return ResponseEntity.ok(taskInfoDTOList);
    }

    //테스크 페이지네이션
    @GetMapping("/menus/{menu-id}/tasks/pages")
    public ResponseEntity<PagingTaskDTO> findTasksByMenuIdWithPagination(
            @PathVariable(name = "menu-id") Long menuId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size //테스크는 20개 기본
    ) {
        PagingTaskDTO pagingTaskDTO = menuService.findTasksByMenuIdWithPagination(menuId, page, size);
        return ResponseEntity.ok(pagingTaskDTO);
    }
//    @GetMapping("/menus/{menu-id}/tasks")
//    public ResponseEntity<List<TaskDTO.TaskInfoDTO>> findTasksByMenuId(@PathVariable("menu-id") Long menuId) {
//        List<TaskDTO.TaskInfoDTO> taskInfoDTOList = menuService.findTasksByMenuId(menuId);
//        return ResponseEntity.ok(taskInfoDTOList);
//    }

    //메뉴별 포스트 가져오기
    @GetMapping("/menus/{menu-id}/posts")
    public ResponseEntity<MenuPostsDTO> findMenuPosts(@PathVariable(name="menu-id") Long menuId) {
        MenuPostsDTO menuPostsDTO = menuService.findPostsInfoByMenuId(menuId);
        return ResponseEntity.ok(menuPostsDTO);
    }

    //포스트 페이지네이션
    @GetMapping("/menus/{menu-id}/posts/pages")
    public ResponseEntity<PagingPostDTO> findPostsByMenuIdWithPagination(
            @PathVariable(name = "menu-id") Long menuId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size //포스트는 20개 기본
    ) {
        PagingPostDTO pagingPostDTO = menuService.findPostsByMenuIdWithPagination(menuId, page, size);
        return ResponseEntity.ok(pagingPostDTO);
    }



}
