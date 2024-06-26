package com.uplog.uplog.domain.menu.dto;

import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.dto.PostDTO;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.dto.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class MenuDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMenuRequest{
        private String menuName;
        //private Long projectId;

        public Menu toEntity(Project project){
            return Menu.builder()
                    .menuName(menuName)
                    .project(project)
                    //.noticePost(null)
                    .build();

        }
    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuInfoDTO{
        private Long id;
        private String menuName;
        private Long projectId;
        private String version;
        private Long noticePostId;
        //private ProjectDTO.requestProjectInfo projectInfo;
        //private PostDTO.PostInfoDTO noticePost;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleMenuInfoDTO{
        private Long id;
        private String menuName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMenuNameRequest{
        //private Long id;
        private String updatemenuName;

    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateNoticePostRequest{
        public Long updateNoticePostId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuTasksDTO {
        private MenuInfoDTO menuInfo;
        private List<TaskDTO.TaskInfoDTO> tasks;
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuPostsDTO {
        private MenuInfoDTO menuInfo;
        private PostDTO.PostDetailInfoDTO noticePost;
        private List<PostDTO.PostDetailInfoDTO> posts;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PagingPostDTO {
        private boolean nextPage;
        private int currentPage;
        private MenuInfoDTO menuInfo;
        private PostDTO.PostDetailInfoDTO noticePost;
        private List<PostDTO.PostDetailInfoDTO> posts;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagingTaskDTO{
        private boolean nextPage;
        private int currentPage;
        //private int taskLength;
        private int progress_before;
        private int progress_in;
        private int progress_complete;
        List<MenuTasksDTO> pagingTaskData=new ArrayList<>();
    }




}
