package com.uplog.uplog.domain.team.dto;

import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.ProjectTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProjectTeamDTO {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CreateProjectTeamRequest{
        private List<Long> memberIdList;
        private String name;
        private Long projectId;
        private Long parentProjectTeamId;

        public ProjectTeam toEntity(List<MemberTeam> memberTeamList, Project project, ProjectTeam parentProjectTeam){
            return ProjectTeam.projectTeamBuilder()
                    .name(this.name)
                    .memberTeamList(memberTeamList)
                    .project(project)
                    .parentTeam(parentProjectTeam)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SimpleProjectTeamInfoDTO{
        private String name;
        private Long projectId;
        private List<Long> memberIdList;
        private List<Long> taskIdList;

    }

//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Getter
//    public static class VerySimpleProjectTeamInfoDTO{
//        private String name;
//        private Long projectId;
//        private List<Long> memberIdList;
//        private List<Long> taskIdList;
//
//    }

    //TODO 리스트로 받는거는 DTO나 이름으로도 고민해보기
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ProjectTeamInfoDTO{
        private String name;
        private Long projectId;
        private List<Long> memberIdList;
        private List<Long> taskIdList;
        private Long parentTeamId;
        private List<Long> childTeamIdList;
    }
}
