package com.uplog.uplog.domain.team.model;

import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.model.Task;
import com.uplog.uplog.domain.team.dto.TeamDTO.TeamInfoDTO;
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

    protected String name;

    public void updateName(String newName){ this.name = newName; }

    public void updateMemberTeamList(List<MemberTeam> memberTeamList){this.memberTeamList = memberTeamList;}

    @Builder
    public Team(Long id, List<MemberTeam> memberTeamList, String name, Project project, Team parentTeam){
        this.id = id;
        this.memberTeamList = memberTeamList;
        this.name = name;
        this.project = project;
        this.parentTeam = parentTeam;
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





}
