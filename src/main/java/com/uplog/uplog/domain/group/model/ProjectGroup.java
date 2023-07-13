package com.uplog.uplog.domain.group.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectGroup extends Group{
    @ManyToOne
    @JoinColumn(name = "parentGroup_id")
    private ProjectGroup parentGroup;
    @OneToMany(mappedBy = "parentGroup")
    private List<ProjectGroup> childGroup = new ArrayList<ProjectGroup>();

}
