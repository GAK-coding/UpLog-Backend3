package com.uplog.uplog.domain.member.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class MemberBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;
    private String name;
    private String nickname;
    private String password;

    @Enumerated(EnumType.STRING)
    private Position position;





    public void updateName(String newName){ this.name = newName;}
    public void updateNickname(String newNickname){this.nickname = newNickname;}
    public void updatePassword(String newPassword){this.password = newPassword;}
    public void updatePosition(Position newPosition){this.position = newPosition;}

}
