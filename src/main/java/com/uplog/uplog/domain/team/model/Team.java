package com.uplog.uplog.domain.team.model;

import com.uplog.uplog.domain.team.dto.TeamDTO.TeamInfoDTO;
import com.uplog.uplog.global.BaseTime;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    protected String name;
    @Builder(builderMethodName = "teamBuilder")
    public Team(Long id, List<MemberTeam> memberTeamList, String name){
        this.id = id;
        this.memberTeamList = memberTeamList;
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

    public void updateName(String newName){ this.name = newName; }



}
