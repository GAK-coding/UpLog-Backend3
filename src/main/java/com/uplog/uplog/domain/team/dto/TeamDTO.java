package com.uplog.uplog.domain.team.dto;

import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeamDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateTeamRequest {
        //TODO 이부분은 좀 더 고려해봐야할듯.
        private List<Long> memberIdList;
        private String name;
        private Long projectId;
        private Long parentTeamId;
        private String link;

        public Team toEntity(Project project, Team parentTeam){
            return Team.builder()
                    //.product(product)
                    .memberTeamList(new ArrayList<>())
                    .project(project)
                    .name(this.name)
                    .parentTeam(parentTeam)
                    .build();

        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamInfoDTO{
        private Long id;
        private String projectName;
        private List<MemberTeam> memberTeamList;
        private LocalDateTime createdTime;
        private LocalDateTime modifiedTime;
    }

}
