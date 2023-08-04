package com.uplog.uplog.domain.project.dto;

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
    public static class CreateInitInfo{


        private Long id;
        private String version;

        public Project toEntity(ProjectStatus projectStatus, Product product){
            return Project.builder()
                    .version(version)
                    .product(product)
                    .projectStatus(projectStatus)
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