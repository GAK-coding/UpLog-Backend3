package com.uplog.uplog.member.model;

import com.uplog.uplog.member.dto.MemberDTO;
import com.uplog.uplog.member.dto.MemberDTO.MemberInfoDTO;
import com.uplog.uplog.member.dto.MemberDTO.SimpleMemberInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends MemberBase{
    private LoginType loginType;


    @Builder
    public Member(Long id, String name, String nickname, String email, String password, Position position, LoginType loginType){
        super(id, email, name, nickname, password, position);
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
}
