package com.uplog.uplog.domain.project.dto;

import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.project.model.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ProjectDTO {


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateInitInfo{


        private Long id;
        private String version;

        public Project toEntity(ProjectStatus projectStatus){
            return Project.builder()
                    .version(version)
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
}