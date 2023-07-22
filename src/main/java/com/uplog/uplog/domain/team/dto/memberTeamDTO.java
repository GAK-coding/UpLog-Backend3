package com.uplog.uplog.domain.team.dto;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.team.model.MemberTeam;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class memberTeamDTO {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SaveMemberTeamRequest{
        private String memberEmail;
        private Long teamId;
        private PowerType powerType;

        public MemberTeam toMemberTeam(Team team, Member member, PowerType powerType){
            return MemberTeam.builder()
                    .team(team)
                    .member(member)
                    .powerType(powerType)
                    .build();
        }

    }
}
