package com.uplog.uplog.domain.member.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Builder
@Table(name="authority")
@NoArgsConstructor
@AllArgsConstructor
public class Authority {

    @JsonIgnore
    @Id
    @Column(name="authority_name",length=50)
    private String authorityName;
}
