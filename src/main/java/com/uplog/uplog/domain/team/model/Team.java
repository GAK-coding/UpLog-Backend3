package com.uplog.uplog.domain.team.model;

import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.team.dto.TeamDTO;
import com.uplog.uplog.domain.team.dto.TeamDTO.TeamInfoDTO;
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
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@DiscriminatorValue("Team")
public class Team extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @OneToMany(mappedBy = "team")
    private List<MemberTeam> memberTeamList = new ArrayList<>();

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product;

    private String name;
    @Builder
    public Team(Long id,Product product, String name){
        this.id = id;
        //this.product = product;
        this.name = name;
    }

    public TeamInfoDTO toTeamInfoDTO(){
        return TeamInfoDTO.builder()
                .id(this.id)
                .productName(this.name)
                .memberTeamList(this.memberTeamList)
                .createdTime(this.getCreatedTime())
                .modifiedTime(this.getModifiedTime())
                .build();
    }



}
