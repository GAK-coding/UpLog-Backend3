package com.uplog.uplog.domain.menu.application;

import com.uplog.uplog.domain.comment.dao.CommentRepository;
import org.springframework.data.domain.Sort;
import com.uplog.uplog.domain.like.dao.PostLikeRepository;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import com.uplog.uplog.domain.menu.exception.*;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.application.PostService;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.dto.PostDTO;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.tag.dto.TagDTO;
import com.uplog.uplog.domain.tag.model.PostTag;
import com.uplog.uplog.domain.task.application.TaskService;
import com.uplog.uplog.domain.task.dto.TaskDTO;
import com.uplog.uplog.domain.task.dto.TaskDTO.*;
import com.uplog.uplog.domain.task.exception.NotFoundTaskByIdException;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final MenuRepository menuRepository;
    private final ProjectRepository projectRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    private final PostService postService;
    private final TaskService taskService;
    /*
    CREATE
     */


    @Transactional
    public List<SimpleMenuInfoDTO> createDefaultMenu(Long projectId) {
        List<String> menuNames = Arrays.asList("결과물", "요구사항", "개발", "배포");
        List<Menu> createdMenus = new ArrayList<>();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundIdException("해당 프로젝트는 존재하지 않습니다."));

        for (String menuName : menuNames) {
            Menu menu = Menu.builder()
                    .menuName(menuName)
                    .project(project)
                    .build();
            createdMenus.add(menu);
        }

        menuRepository.saveAll(createdMenus);
        project.getMenuList().addAll(createdMenus);

        List<SimpleMenuInfoDTO> menuInfoDTOList = createdMenus.stream()
                .map(Menu::toSimpleMenuInfoDTO)
                .collect(Collectors.toList());

        return menuInfoDTOList;
    }

    @Transactional
    public Long createMenu(Long projectId, @RequestBody CreateMenuRequest createMenuRequest) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(NotFoundIdException::new);


        // 중복 검사
        String menuName = createMenuRequest.getMenuName();
        List<Menu> existingMenus = menuRepository.findByProjectIdAndMenuName(projectId, menuName);
        if (!existingMenus.isEmpty()) {
            throw new DuplicatedMenuNameInProjectException();
        }

        //메뉴 최대 15개
        if (menuRepository.countByProjectId(projectId) >= 15) {
            throw new ExceededMaxMenuCountException();
        }


        Menu menu = createMenuRequest.toEntity(project);
        menuRepository.save(menu);
        return menu.toMenuInfoDTO().getId();
    }

    /*
    DELETE
     */
    @Transactional
    public String deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(NotFoundIdException::new);
        if ("결과물".equals(menu.getMenuName())) {
            throw new MenuUpdateNotAllowedException();
        }
        menuRepository.delete(menu);
        return "delete";
    }

    /*
    UPDATE
     */
    @Transactional
    public MenuInfoDTO updateMenuName(Long id, UpdateMenuNameRequest updateMenuNameRequest) {
        Menu menu = menuRepository.findById(id).orElseThrow(NotFoundIdException::new);

        String updateName = updateMenuNameRequest.getUpdatemenuName();

        //결과물은 수정 안돼
        if ("결과물".equals(menu.getMenuName())) {
            throw new MenuUpdateNotAllowedException();
        }

        // 기존 메뉴 이름과 업데이트하고자 하는 이름이 같은 경우
        if (menu.getMenuName().equals(updateName)) {
            throw new DuplicatedMenuNameInProjectException("현재 메뉴이름과 동일합니다.");
        }

        // 중복 검사
        List<Menu> existingMenus = menuRepository.findByProjectIdAndMenuName(menu.getProject().getId(), updateName);

        if (!existingMenus.isEmpty()) {
            throw new DuplicatedMenuNameInProjectException();
        }

        menu.updateMenuName(updateMenuNameRequest.getUpdatemenuName());
        return menu.toMenuInfoDTO();
    }

    @Transactional
    public MenuInfoDTO updateNoticePost(Long menuId, UpdateNoticePostRequest updateNoticePostRequest) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(NotFoundIdException::new);
        Post post = postRepository.findById(updateNoticePostRequest.getUpdateNoticePostId()).orElseThrow(NotFoundTaskByIdException::new);
        if (!menuId.equals(post.getMenu().getId())) {
            throw new MenuUpdateNotAllowedException("해당 포스트의 메뉴아이디와 일치하지 않아서 공지글로 등록할 수 없습니다");
        }
        menu.updateNoticePost(post);
        return menu.toMenuInfoDTO();
    }

    @Transactional
    public String deleteNoticePost(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(NotFoundIdException::new);
        menu.updateNoticePost(null);
        return "delete";
    }



    /*
    READ
     */

    //프로젝트의 메뉴만 가져오는거
    @Transactional(readOnly = true)
    public List<MenuInfoDTO> findByProjectId(Long projectId) {
        List<Menu> menuList = menuRepository.findByProjectId(projectId);
        List<MenuInfoDTO> menuInfoDTOS = new ArrayList<>();
        for (Menu menu : menuList) {
            MenuInfoDTO menuInfoDTO = menu.toMenuInfoDTO();
            menuInfoDTOS.add(menuInfoDTO);
        }
        return menuInfoDTOS;
    }

    //해당 아이디의 메뉴 가져오는거
    @Transactional(readOnly = true)
    public MenuInfoDTO findMenuById(Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow(NotFoundIdException::new);
        return menu.toMenuInfoDTO();

    }

    //해당 메뉴의 테스크 가져오는거
    @Transactional(readOnly = true)
    public List<TaskInfoDTO> findTasksByMenuId(Long menuId) {
        List<Task> taskList = taskService.findByMenuId(menuId);
        return taskList.stream()
                .map(Task::toTaskInfoDTO)
                .collect(Collectors.toList());
    }

    //테스크 페이지네이션
    @Transactional(readOnly = true)
    public PagingTaskDTO findTasksByMenuIdWithPagination(Long menuId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Task> taskPage = taskService.findPageByMenuId(menuId, pageable);

        boolean nextPage = taskPage.hasNext();

        List<TaskDTO.TaskInfoDTO> taskInfoDTOList = taskPage.getContent().stream()
                .map(Task::toTaskInfoDTO)
                .collect(Collectors.toList());

        MenuInfoDTO menuInfoDTO = findMenuById(menuId);
        MenuTasksDTO menuTasksDTO = new MenuTasksDTO(menuInfoDTO, taskInfoDTOList);

        return new PagingTaskDTO(nextPage, Collections.singletonList(menuTasksDTO));
    }


    //해당 메뉴의 포스트 가져오는거(공지글 포함)
    @Transactional(readOnly = true)
    public List<PostDTO.PostDetailInfoDTO> findPostsByMenuId(Long menuId) {
        List<Post> postList = postService.findPostsByMenuId(menuId);
        List<PostDTO.PostDetailInfoDTO> postInfoDTOs = new ArrayList<>();
        for (Post post : postList) {
            int likeCount = postLikeRepository.countByPostId(post.getId());
            int commentCount = commentRepository.countByPostId(post.getId());

            List<TagDTO.TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
            for (PostTag pt : post.getPostTagList()) {
                postTags.add(pt.getTag().toTagInfoDTO());
            }
            postInfoDTOs.add(post.toPostDetailInfoDTO(postTags, likeCount, commentCount));
        }
        return postInfoDTOs;
//        return postList.stream()
//
//                .map(Post::toPostDetailInfoDTO())
//                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public PagingPostDTO findPostsByMenuIdWithPagination(Long menuId, int page, int size) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(NotFoundIdException::new);
        Post noticePost = menu.getNoticePost();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Post> postPage = postService.findPageByMenuId(menuId, pageable);
        boolean nextPage = postPage.hasNext();

        List<PostDTO.PostDetailInfoDTO> postInfoDTOs = new ArrayList<>();

        for (Post post : postPage.getContent()) {
            int likeCount = postLikeRepository.countByPostId(post.getId());
            int commentCount = commentRepository.countByPostId(post.getId());

            List<TagDTO.TagInfoDTO> postTags = new ArrayList<>();
            for (PostTag pt : post.getPostTagList()) {
                postTags.add(pt.getTag().toTagInfoDTO());
            }

            postInfoDTOs.add(post.toPostDetailInfoDTO(postTags, likeCount, commentCount));
        }

        MenuInfoDTO menuInfoDTO = menu.toMenuInfoDTO();

        PostDTO.PostDetailInfoDTO noticePostDTO = null;
        if (noticePost != null) {
            int likeCount = postLikeRepository.countByPostId(noticePost.getId());
            int commentCount = commentRepository.countByPostId(noticePost.getId());

            List<TagDTO.TagInfoDTO> postTags = new ArrayList<>();
            for (PostTag pt : noticePost.getPostTagList()) {
                postTags.add(pt.getTag().toTagInfoDTO());
            }

            noticePostDTO = noticePost.toPostDetailInfoDTO(postTags, likeCount, commentCount);
        }

        return new PagingPostDTO(nextPage, menuInfoDTO, noticePostDTO, postInfoDTOs);
    }


    @Transactional(readOnly = true)
    public MenuPostsDTO findPostsInfoByMenuId(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(NotFoundIdException::new);
        Post noticePost = menu.getNoticePost();
        if (noticePost != null) {
            int likeCount = postLikeRepository.countByPostId(noticePost.getId());
            int commentCount = commentRepository.countByPostId(noticePost.getId());

            List<TagDTO.TagInfoDTO> postTags = new ArrayList<>(); // PostTag 리스트 생성
            for (PostTag pt : noticePost.getPostTagList()) {
                postTags.add(pt.getTag().toTagInfoDTO());
            }
            PostDTO.PostDetailInfoDTO noticePostDTO = noticePost.toPostDetailInfoDTO(postTags, likeCount, commentCount);
            List<Post> posts = postService.findPostsByMenuId(menuId);

            MenuInfoDTO menuInfoDTO = menu.toMenuInfoDTO();
            List<Post> postList2 = postService.findPostsByMenuId(menuId);
            List<PostDTO.PostDetailInfoDTO> postInfoDTOs2 = new ArrayList<>();
            for (Post post : postList2) {
                int likeCount2 = postLikeRepository.countByPostId(post.getId());
                int commentCount2 = commentRepository.countByPostId(post.getId());

                List<TagDTO.TagInfoDTO> postTags2 = new ArrayList<>(); // PostTag 리스트 생성
                for (PostTag pt : post.getPostTagList()) {
                    postTags2.add(pt.getTag().toTagInfoDTO());
                }
                postInfoDTOs2.add(post.toPostDetailInfoDTO(postTags2, likeCount2, commentCount2));
            }
            return new MenuPostsDTO(menuInfoDTO, noticePostDTO, postInfoDTOs2);
        } else {
            MenuInfoDTO menuInfoDTO = menu.toMenuInfoDTO();
            return new MenuPostsDTO(menuInfoDTO, null, findPostsByMenuId(menuId));
        }
    }


    }


