package com.uplog.uplog.domain.team.model;


import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberTeamInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
//@AllArgsConstructor
@NoArgsConstructor
//@Inheritance(strategy = InheritanceType.JOINED)
public class MemberTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberTeam_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private PowerType powerType;

    @Builder
    public MemberTeam(Long id, Team team, Member member, PowerType powerType){
        this.id = id;
        this.team = team;
        this.member = member;
        this.powerType = powerType;
    }

    public MemberTeamInfoDTO toMemberTeamInfoDTO(){
        return MemberTeamInfoDTO.builder()
                .id(this.id)
                .memberId(this.getMember().getId())
                .memberName(this.getMember().getName())
                .teamId(this.getTeam().getId())
                .build();
    }

    public void updatePowerType(PowerType newPowerType){ this.powerType = newPowerType; }


}
