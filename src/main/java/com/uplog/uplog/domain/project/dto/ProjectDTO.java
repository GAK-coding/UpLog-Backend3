package com.uplog.uplog.domain.project.dto;

import com.uplog.uplog.domain.project.model.Project;
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

        public Project of(){
            return Project.builder()
                    .version(version)
                    .build();
        }

    }
}
