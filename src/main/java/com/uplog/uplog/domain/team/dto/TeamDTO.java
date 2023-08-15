package com.uplog.uplog.domain.team.dto;

import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.SimpleMemberPowerInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.TeamAndPowerTypeDTO;
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
        //private Long projectId;
        private Long parentTeamId;
        private String link;

        public Team toEntity(Project project, Team parentTeam, int depth){
            return Team.builder()
                    //.product(product)
                    .memberTeamList(new ArrayList<>())
                    .project(project)
                    .name(this.name)
                    .parentTeam(parentTeam)
                    .depth(depth)
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

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleTeamInfoDTO{
        private Long id;
        private String teamName;
        private int depth;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateTeamResultDTO{
        private Long id;
        private List<Long> DuplicatedMemberList;
    }
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddMemberToTeamRequest{
        private List<Long> addMemberIdList;
        private String link;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddMemberTeamResultDTO{
        private Long id;
        private List<Long> DuplicatedMemberList;
        List<MemberPowerDTO> MemberPowerDTO;
    }

    //프로젝트와 멤버아이디로 member가 속한 팀 찾아서 뱉어주는 디티오
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamsBysMemberAndProject{
        private String memberName;
        private String memberNickname;
        private String projectName;
        private List<TeamAndPowerTypeDTO> teamAndPowerTypeDTOList;
    }

}
