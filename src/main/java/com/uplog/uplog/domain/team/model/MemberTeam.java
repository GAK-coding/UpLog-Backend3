package com.uplog.uplog.domain.team.model;


import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.SimpleTeamInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberPowerDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.MemberTeamInfoDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.TeamAndPowerTypeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.build.Plugin;

import javax.persistence.*;

@Entity
@Getter
//@AllArgsConstructor
@NoArgsConstructor
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

    private boolean delStatus;

    public void updateDelStatus(boolean delStatus){ this.delStatus = delStatus; }
    public void updatePowerType(PowerType newPowerType){ this.powerType = newPowerType; }

    public void updateTeam(Team team){this.team = team;}

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
                .memberId(this.member.getId())
                .memberName(this.member.getName())
                .memberNickname(this.member.getNickname())
                .teamId(this.team.getId())
                .delStatus(this.delStatus)
                .build();
    }

    public TeamAndPowerTypeDTO toTeamAndPowerTypeDTO(SimpleTeamInfoDTO simpleTeamInfoDTO){
        return TeamAndPowerTypeDTO.builder()
                .simpleTeamInfoDTO(simpleTeamInfoDTO)
                .powerType(this.powerType)
                .delStatus(this.delStatus)
                .build();
    }

    public MemberPowerDTO toMemberPowerDTO(){
        return MemberPowerDTO.builder()
                .memberId(this.member.getId())
                .memberEmail(this.member.getEmail())
                .memberName(this.member.getName())
                .memberNickname(this.member.getNickname())
                .powerType(this.powerType)
                .delStatus(this.delStatus)
                .build();

    }




}

