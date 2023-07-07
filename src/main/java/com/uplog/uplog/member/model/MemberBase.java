package com.uplog.uplog.member.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
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

}
