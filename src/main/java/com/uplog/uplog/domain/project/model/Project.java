package com.uplog.uplog.domain.project.model;

import com.uplog.uplog.domain.changedIssue.dto.ChangedIssueDTO;
import com.uplog.uplog.domain.member.model.Position;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.team.model.ProjectTeam;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Project extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @OneToMany(mappedBy = "project")
    private List<ProjectTeam> projectTeamList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "project")
    private List<Menu> menuList = new ArrayList<>();

    private String version;

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;

    @Builder
    public Project(List<ProjectTeam> projectTeamList, Product product, List<Menu> menuList, String version, ProjectStatus projectStatus){

        this.projectTeamList=projectTeamList;
        this.product=product;
        this.menuList=menuList;
        this.version=version;
        this.projectStatus=projectStatus;

    }

    public ProjectDTO.CreateInitInfo toCreateInitChangedIssueInfo(){
        return ProjectDTO.CreateInitInfo.builder()
                .id(this.id)
                .version(this.version)
                .build();
    }

    public ProjectDTO.UpdateProjectInfo toUpdateProjectInfo(){
        return ProjectDTO.UpdateProjectInfo.builder()
                .id(this.id)
                .version(this.version)
                .projectStatus(this.projectStatus)
                .build();
    }

    public void updateProjectStatus(ProjectDTO.UpdateProjectStatus updateProjectStatus){
        this.version=(updateProjectStatus.getVersion()!=null)?updateProjectStatus.getVersion():this.version;
        this.projectStatus=ProjectStatus.PROGRESS_COMPLETE;
    }
}