package com.uplog.uplog.domain.team.model;

import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.model.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTeam extends Team {
    @OneToMany(mappedBy = "projectTeam")
    private List<Task> taskList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentTeam_id")
    private ProjectTeam parentTeam;

    @OneToMany(mappedBy = "parentTeam")
    private List<ProjectTeam> childTeam = new ArrayList<ProjectTeam>();

}
