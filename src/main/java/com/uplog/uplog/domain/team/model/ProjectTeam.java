//package com.uplog.uplog.domain.team.model;
//
//import com.uplog.uplog.domain.project.model.Project;
//import com.uplog.uplog.domain.task.model.Task;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.experimental.SuperBuilder;
//
//import javax.persistence.*;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor
//@DiscriminatorValue("Project_Team")
//public class ProjectTeam extends Team {
//    @OneToMany(mappedBy = "projectTeam")
//    private List<Task> taskList = new ArrayList<>();
//
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    private Project project;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parentTeam_id")
//    private ProjectTeam parentTeam;
//
//    @OneToMany(mappedBy = "parentTeam")
//    private List<ProjectTeam> childTeamList = new ArrayList<ProjectTeam>();
//
//    @Builder(builderMethodName = "projectTeamBuilder")
//    public ProjectTeam(Long id, List<MemberTeam> memberTeamList, String name, Project project, ProjectTeam parentTeam){
//        super(id, memberTeamList, name);
//        this.project = project;
//        this.parentTeam = parentTeam;
//    }
//}
