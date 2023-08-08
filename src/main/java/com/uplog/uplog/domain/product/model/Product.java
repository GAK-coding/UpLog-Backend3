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

    private String name;

    //drag/drop을 위해 추가된 변수. 아이디값이 담김.
    //맨 처음 생기는 product는 parent가 null일 수 밖에 없음,
    private Long parentIndex;

    public void updateTeam(Team team){this.team = team;}

    public void updateCompany(String company){this.company = company;}

    public void updateName(String name){this.name = name;}

    public void addTeamToProduct(Team team){this.team = team;}


    @Builder
    public Product(Long id, Team team, String company, String name){
        this.id = id;
        this.team = team;
        this.company = company;
        this.name = name;
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
