package com.uplog.uplog.domain.product.model;

import com.uplog.uplog.domain.product.dto.ProductDTO;
import com.uplog.uplog.domain.product.dto.ProductDTO.ProductInfoDTO;
import com.uplog.uplog.domain.project.model.Project;
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
public class Product extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "product")
    private List<Project> projectList = new ArrayList<>();

    private String company;

    //기업을 필드로 저장
    private Long companyId;

    private String name;

    //drag/drop을 위해 추가된 변수.
    private Long index;

    public void updateTeam(Team team){this.team = team;}

    public void updateCompany(String company){this.company = company;}

    public void updateName(String name){this.name = name;}

    public void addTeamToProduct(Team team){this.team = team;}


    @Builder
    public Product(Long id, Team team, String company, Long companyId, String name, Long index){
        this.id = id;
        this.team = team;
        this.company = company;
        this.companyId = companyId;
        this.name = name;
        this.index = index;
    }

    public ProductInfoDTO toProductInfoDTO(List<Long> projectListId){
        return ProductInfoDTO.builder()
                .id(this.id)
                .name(this.name)
                .company(this.company)
                .teamId(this.team.getId())
                .projectListId(projectListId)
                .build();
    }



}
