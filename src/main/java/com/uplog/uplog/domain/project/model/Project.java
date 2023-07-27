package com.uplog.uplog.domain.project.model;

import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.product.model.Product;
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

    @Builder
    public Project(List<ProjectTeam> projectTeamList, Product product, List<Menu> menuList, String version){

        this.projectTeamList=projectTeamList;
        this.product=product;
        this.menuList=menuList;
        this.version=version;

    }
}