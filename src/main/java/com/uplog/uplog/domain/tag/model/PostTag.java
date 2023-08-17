package com.uplog.uplog.domain.tag.model;


import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.domain.tag.dto.TagDTO;
import com.uplog.uplog.domain.tag.dto.TagDTO.PostTagInfoDTO;
import com.uplog.uplog.global.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class PostTag extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postTag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    public PostTag(Long id, Post post,Tag tag){
        this.id = id;
        this.post=post;
        this.tag=tag;
    }

    public PostTagInfoDTO toPostTagInfoDTO(){
        return PostTagInfoDTO.builder()
                .id(this.id)
                .tagId(this.getTag().getId())
                .postId(this.getPost().getId())
                .content(this.getTag().getContent())
                .build();
    }



}
