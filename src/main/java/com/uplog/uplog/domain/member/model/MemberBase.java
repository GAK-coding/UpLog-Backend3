package com.uplog.uplog.domain.member.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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

    @Column(name = "email", length = 50, unique = true)
    private String email;

    private String name;
    private String nickname;
    private String password;

    @Enumerated(EnumType.STRING)
    private Position position;


    @ManyToMany
    @JoinTable(
            name="user_authority",
            joinColumns={@JoinColumn(name="member_id",referencedColumnName = "member_id")},
            inverseJoinColumns={@JoinColumn(name="authority_name",referencedColumnName="authority_name")})
    private Set<Authority> authorities;



    public void updateName(String newName){ this.name = newName;}
    public void updateNickname(String newNickname){this.nickname = newNickname;}
    public void updatePassword(String newPassword){this.password = newPassword;}
    public void updatePosition(Position newPosition){this.position = newPosition;}

}
