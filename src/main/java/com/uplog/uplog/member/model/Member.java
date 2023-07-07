package com.uplog.uplog.member.model;

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
    private MemberStatus memberStatus;


    @Builder
    public Member(Long id, String name, String nickname, String email, String password, Position position, MemberStatus memberStatus, LoginType loginType){
        super(id, email, name, nickname, password, position);
        this.memberStatus = memberStatus;
        this.loginType = loginType;

    }
}
