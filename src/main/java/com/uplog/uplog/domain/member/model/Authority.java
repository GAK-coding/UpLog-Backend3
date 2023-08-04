package com.uplog.uplog.domain.member.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;


@Entity
@Table(name = "authority")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {


    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;
}
