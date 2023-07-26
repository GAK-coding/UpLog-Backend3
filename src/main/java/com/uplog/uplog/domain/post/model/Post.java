package com.uplog.uplog.domain.post.model;

import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.tag.model.PostTag;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.uplog.uplog.domain.post.dto.PostDTO.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
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

    @OneToMany
    @JoinColumn(name = "postTag_id")
    private List<PostTag> postTagList = new ArrayList<>();

    private String title;
    //content에 image랑 codeblock이 한번에 포함된것
    private String content;
    private String productName;
    private String version;
    private LocalDateTime createTime;


    public PostInfoDTO toPostInfoDTO(){
        return PostInfoDTO.builder()
                .id(this.getId())
                .title(this.getTitle())
                .authorInfoDTO(this.getAuthor().powerMemberInfoDTO())
                .menuId(this.getMenu().getId())
                .menuName(this.getMenu().getMenuName())
                .productName(this.getProductName())
                .projectName(this.getVersion())
                .postType(this.getPostType())
                .content(this.getContent())
                .createTime(this.getCreateTime())
                .build();
    }

}
