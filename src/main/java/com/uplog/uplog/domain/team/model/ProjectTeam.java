package com.uplog.uplog.domain.team.model;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentTeam_id")
    private ProjectTeam parentTeam;

    @OneToMany(mappedBy = "parentTeam")
    private List<ProjectTeam> childTeam = new ArrayList<ProjectTeam>();

}
