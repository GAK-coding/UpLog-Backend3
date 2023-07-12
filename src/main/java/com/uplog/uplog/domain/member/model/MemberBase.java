package com.uplog.uplog.domain.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Inheritance(strategy = InheritanceType.JOINED)
public class MemberBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private long id;

    private String email;
    private String name;
    private String nickname;
    private String password;
    private Position position;

    public void changeName(String newName){ this.name = newName;}
    public void changeNickname(String newNickname){this.nickname = newNickname;}
    public void changePassword(String newPassword){this.password = newPassword;}
    public void changePosition(Position newPosition){this.position = newPosition;}

}
