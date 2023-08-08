package com.uplog.uplog.domain.team.dto;

import com.uplog.uplog.domain.product.model.Product;
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
        private String memberEmail;
        //private Long productId;
        private String teamName;
        //팀의 이름은 제품의 이름으로 들어가게 됨!
        private String link;
        private int mailType;

        public Team toEntity(){
            return Team.teamBuilder()
                    //.product(product)
                    .memberTeamList(new ArrayList<>())
                    .name(this.teamName)
                    .build();

        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamInfoDTO{
        private Long id;
        //private Long productId;
        private String productName;
        private List<MemberTeam> memberTeamList;
        private LocalDateTime createdTime;
        private LocalDateTime modifiedTime;
    }

}
