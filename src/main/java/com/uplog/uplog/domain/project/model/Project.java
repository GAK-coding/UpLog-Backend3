package com.uplog.uplog.domain.project.model;

import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.dto.MenuDTO.SimpleMenuInfoDTO;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.dto.ProjectDTO.ProjectInfoDTO;
import com.uplog.uplog.domain.project.dto.ProjectDTO.SimpleProjectInfoDTO;
import com.uplog.uplog.domain.team.model.PowerType;
import com.uplog.uplog.domain.team.model.Team;
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
    private List<Team> teamList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "project")
    private List<Menu> menuList = new ArrayList<>();

    private String version; //project이름

    @Enumerated(EnumType.STRING)
    private ProjectStatus projectStatus;

    @Builder
    public Project(List<Team> teamList, Product product, List<Menu> menuList, String version, ProjectStatus projectStatus){

        this.teamList=teamList;
        this.product=product;
        this.menuList=menuList;
        this.version=version;
        this.projectStatus=projectStatus;

    }

    public SimpleProjectInfoDTO toSimpleProjectTeamInfoDTO(List<SimpleMenuInfoDTO> menuList){
        return SimpleProjectInfoDTO.builder()
                .id(this.id)
                .version(this.version)
                .productId(this.product.getId())
                .menuList(menuList)
                .build();
    }

    public ProjectInfoDTO toProjectInfoDTO(List<SimpleMenuInfoDTO> simpleMenuInfoDTOList, List<Long> projectTeamIdList){
        return ProjectInfoDTO.builder()
                .id(this.id)
                .version(this.version)
                .projectTeamIdList(projectTeamIdList)
                .projectStatus(this.projectStatus)
                .productId(this.product.getId())
                .menuList(simpleMenuInfoDTOList)
                .build();

    }

    public ProjectDTO.CreateProjectRequest toCreateInitChangedIssueInfo(){
        return ProjectDTO.CreateProjectRequest.builder()
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

    public ProjectDTO.requestProjectAllInfo toRequestProjectAllInfo(PowerType powerType, String productName, String company){
        return ProjectDTO.requestProjectAllInfo.builder()
                .projectId(this.id)
                //.teamList(this.teamList)
                .menuList(this.menuList)
                .version(this.version)
                .projectStatus(this.projectStatus)
                .powerType(powerType)
                .productName(productName)
                .company(company)
                .build();
    }
    public ProjectDTO.requestProjectInfo toRequestProjectInfo(PowerType powerType,String productName, String company){
        return ProjectDTO.requestProjectInfo.builder()
                .productName(productName)
                .company(company)
                .version(this.version)
                .powerType(powerType)
                .build();
    }

    public void updateProjectStatus(ProjectDTO.UpdateProjectStatus updateProjectStatus){
        this.version=(updateProjectStatus.getVersion()!=null)?updateProjectStatus.getVersion():this.version;
        this.projectStatus=ProjectStatus.PROGRESS_COMPLETE;
    }
}