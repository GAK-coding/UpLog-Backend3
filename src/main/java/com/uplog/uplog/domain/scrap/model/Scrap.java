package com.uplog.uplog.domain.scrap.model;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Scrap extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "scrap")
    private Member member;

    @OneToMany(mappedBy = "scrap")
    private List<Post> postList = new ArrayList<>();
}
