package com.uplog.uplog.domain.team.model;

import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.VerySimpleMemberInfoDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.*;
import com.uplog.uplog.domain.team.dto.memberTeamDTO;
import com.uplog.uplog.domain.team.dto.memberTeamDTO.TeamAndPowerTypeDTO;
import com.uplog.uplog.global.BaseTime;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Team extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @OneToMany(mappedBy = "team")
    private List<MemberTeam> memberTeamList = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<Task> taskList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentTeam_id")
    private Team parentTeam;

    @OneToMany(mappedBy = "parentTeam")
    private List<Team> childTeamList = new ArrayList<Team>();

    private String name;

    private int depth;

    private boolean delStatus;


    public void updateName(String newName){ this.name = newName; }

    public void updateMemberTeamList(List<MemberTeam> memberTeamList){this.memberTeamList = memberTeamList;}
    public void updateDelStatus(boolean b){this.delStatus = b;}

    @Builder
    public Team(Long id, List<MemberTeam> memberTeamList, String name, Project project, Team parentTeam, int depth){
        this.id = id;
        this.memberTeamList = memberTeamList;
        this.name = name;
        this.project = project;
        this.parentTeam = parentTeam;
        this.depth = depth;
        this.delStatus = false;
    }


    public TeamInfoDTO toTeamInfoDTO(){
        return TeamInfoDTO.builder()
                .id(this.id)
                .projectName(this.project.getVersion())
                .memberTeamList(this.memberTeamList)
                .createdTime(this.getCreatedTime())
                .modifiedTime(this.getModifiedTime())
                .build();
    }

    public TeamsBysMemberAndProject toTeamsByMemberAndProject(String memberName, String memberNickname, String projectName, List<TeamAndPowerTypeDTO> simpleTeamInfoDTOList){
        return TeamsBysMemberAndProject.builder()
                .memberName(memberName)
                .memberNickname(memberNickname)
                .projectName(projectName)
                .teamAndPowerTypeDTOList(simpleTeamInfoDTOList)
                .build();
    }

    public SimpleTeamInfoDTO toSimpleTeamInfoDTO(){
        return SimpleTeamInfoDTO.builder()
                .id(this.id)
                .teamName(this.name)
                .depth(this.depth)
                .build();
    }
    public SimpleTeamIncludeChildInfoDTO toSimpleTeamIncludeChildInfoDTO(List<SimpleTeamIncludeChildInfoDTO> childTeamInfoDTOList){
        return SimpleTeamIncludeChildInfoDTO.builder()
                .teamId(this.id)
                .teamName(this.name)
                .depth(this.depth)
                .childTeamInfoDTOList(childTeamInfoDTOList)
                .build();
    }

    public TeamIncludeChildInfoDTO toTeamIncludeChildInfoDTO(List<SimpleTeamIncludeChildInfoDTO> childTeamInfoDTOList){
        return TeamIncludeChildInfoDTO.builder()
                .projectName(this.project.getVersion())
                .teamId(this.id)
                .teamName(this.name)
                .depth(this.depth)
                .childTeamInfoDTOList(childTeamInfoDTOList)
                .build();

    }

    public TeamIncludeChildWithMemberInfoDTO toTeamIncludeChildWithMemberInfoDTO(List<VerySimpleMemberInfoDTO> verySimpleMemberInfoDTOList, List<SimpleTeamIncludeChildWithMemberInfoDTO> childTeamWithMemberInfoDTOList){
        return TeamIncludeChildWithMemberInfoDTO.builder()
                .projectName(this.project.getVersion())
                .teamId(this.id)
                .teamName(this.name)
                .depth(this.depth)
                .verySimpleMemberInfoDTOList(verySimpleMemberInfoDTOList)
                .childTeamInfoDTOList(childTeamWithMemberInfoDTOList)
                .build();
    }

    public SimpleTeamIncludeChildWithMemberInfoDTO toSimpleTeamIncludeChildWithMemberInfoDTO(List<VerySimpleMemberInfoDTO> verySimpleMemberInfoDTOList, List<SimpleTeamIncludeChildWithMemberInfoDTO> childTeamWithMemberInfoDTOList){
        return SimpleTeamIncludeChildWithMemberInfoDTO.builder()
                .teamId(this.id)
                .teamName(this.name)
                .depth(this.depth)
                .verySimpleMemberInfoDTOList(verySimpleMemberInfoDTOList)
                .childTeamInfoDTOList(childTeamWithMemberInfoDTOList)
                .build();
    }

    public TeamWithMemberAndChildTeamInfoDTO toTeamWithMemberAndChildTeamInfoDTO(List<VerySimpleMemberInfoDTO> verySimpleMemberInfoDTOList, List<SimpleTeamInfoDTO> simpleTeamInfoDTOList){
        return TeamWithMemberAndChildTeamInfoDTO.builder()
                .teamId(this.id)
                .teamName(this.name)
                .depth(this.depth)
                .verySimpleMemberInfoDTOList(verySimpleMemberInfoDTOList)
                .childTeamInfoDTOList(simpleTeamInfoDTOList)
                .build();
    }
}
