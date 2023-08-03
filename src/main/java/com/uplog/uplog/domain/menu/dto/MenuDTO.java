package com.uplog.uplog.domain.menu.dto;

import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.dto.PostDTO;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        //private ProjectDTO.requestProjectInfo projectInfo;
        private PostDTO.PostInfoDTO noticePost;
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





}
