package com.uplog.uplog.domain.team.model;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.SimpleTeamInfoDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.TeamInfoDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.TeamsBysMemberAndProject;
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


    public void updateName(String newName){ this.name = newName; }

    public void updateMemberTeamList(List<MemberTeam> memberTeamList){this.memberTeamList = memberTeamList;}

    @Builder
    public Team(Long id, List<MemberTeam> memberTeamList, String name, Project project, Team parentTeam, int depth){
        this.id = id;
        this.memberTeamList = memberTeamList;
        this.name = name;
        this.project = project;
        this.parentTeam = parentTeam;
        this.depth = depth;
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
                .build();
    }
}
