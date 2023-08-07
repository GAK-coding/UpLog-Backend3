package com.uplog.uplog.domain.project.dto;

import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.dto.MenuDTO.SimpleMenuInfoDTO;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.ProjectTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProjectDTO {


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProjectRequest{

        //private Long id;
        private String version;
        private String link;

        public Project toEntity(Product product){
            return Project.builder()
                    .version(this.version)
                    .product(product)
                    .projectStatus(ProjectStatus.PROGRESS_IN)
                    .build();
        }

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProjectStatus{
        private String version;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProjectInfo{
        private Long id;
        private String version;
        private ProjectStatus projectStatus;

    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleProjectInfoDTO{
        private Long id;
        private String version;
        private Long productId;
        private List<SimpleMenuInfoDTO> menuList;
        private ProjectStatus projectStatus;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectInfoDTO{
        private Long id;
        private String version;
        private Long productId;
        private List<SimpleMenuInfoDTO> menuList;
        private List<Long> projectTeamIdList;
        private ProjectStatus projectStatus;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class requestProjectAllInfo {
        private Long productId;
        private String productName;
        private String company;
        private Long projectId;
        private List<ProjectTeam> projectTeamList;
        private List<Menu> menuList;
        private String version;
        private ProjectStatus projectStatus;
        private PowerType powerType;


    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class requestProjectInfo{

        private String productName;
        private String company;
        private String version;
        private PowerType powerType;
    }
}