package com.uplog.uplog.domain.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uplog.uplog.domain.comment.dto.CommentDTO;
import com.uplog.uplog.domain.comment.dto.CommentDTO.VerySimpleCommentInfoDTO;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.global.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "member_id")
    private Member author;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> childList ;

    private String content;

    @Builder
    public Comment(Long id, Post post, Member member, Comment parent, List<Comment> childList, String content){
        this.id=id;
        this.post=post;
        this.author=member;
        this.parent=parent;
        this.childList=childList;
        this.content=content;
    }
    public CommentDTO.SimpleCommentInfo toSimpleCommentInfo(String image){
        return CommentDTO.SimpleCommentInfo.builder()
                .content(this.content)
                .id(this.id)
                .image(image)
                .nickName((this.author==null)?null:this.author.getNickname())
                .name((this.author==null)?null:this.author.getName())
                .memberId((this.author==null)?null:this.author.getId())
                .createTime(this.getCreatedTime())
                .parentId((this.parent==null)?null:this.parent.getId())
                .memberId(this.author.getId())
                .build();
    }

    public VerySimpleCommentInfoDTO toVerySimpleCommentInfoDTO(){
        return VerySimpleCommentInfoDTO.builder()
                .id(this.id)
                .content(this.content)
                .build();
    }


    public void updateCommentContent(String content){
        this.content=content;
    }



}