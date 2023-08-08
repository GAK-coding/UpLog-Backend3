package com.uplog.uplog.domain.member.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uplog.uplog.domain.chatting.model.MemberChattingRoom;
import com.uplog.uplog.domain.member.dto.MemberDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.MemberInfoDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.PowerMemberInfoDTO;
import com.uplog.uplog.domain.member.dto.MemberDTO.SimpleMemberInfoDTO;
import com.uplog.uplog.domain.product.model.Product;
import com.uplog.uplog.domain.scrap.model.Scrap;
import com.uplog.uplog.domain.team.model.MemberTeam;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends MemberBase{
    @Enumerated(EnumType.STRING)
    private LoginType loginType;


    //TODO 의논할 것 -> product list대신 얘를 갖고 있는것 어떤지
    @OneToMany(mappedBy = "member")
    private List<MemberTeam> memberTeamList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberChattingRoom> memberChattingRoomList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id")
    private Scrap scrap;




    @Column(name="activated")
    private Boolean activated;
    @Builder
    public Member(Long id, String email, String name, String nickname, String password, Position position, LoginType loginType,Set<Authority> authorities){
        super(id, email, name, nickname, password, position,authorities);
        this.loginType = loginType;


    }

    public MemberInfoDTO toMemberInfoDTO(){
        return MemberInfoDTO.builder()
                .id(this.getId())
                .email(this.getEmail())
                .name(this.getName())
                .nickname(this.getNickname())
                .password(this.getPassword())
                .position(this.getPosition())
                .build();
    }


    public SimpleMemberInfoDTO simpleMemberInfoDTO(){
        return SimpleMemberInfoDTO.builder()
                .id(this.getId())
                .name(this.getName())
                .nickname(this.getNickname())
                .password(this.getPassword())
                .build();
    }

    public PowerMemberInfoDTO powerMemberInfoDTO(){
        return PowerMemberInfoDTO.builder()
                .id(this.getId())
                .name(this.getName())
                .nickname(this.getNickname())
                .position(this.getPosition())
                .build();
    }
}
