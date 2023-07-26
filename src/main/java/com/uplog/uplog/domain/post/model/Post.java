package com.uplog.uplog.domain.post.model;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    private String title;
    private String content;
    private String projectName;
    private String version;

}
