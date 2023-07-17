package com.uplog.uplog.domain.like.model;

import com.uplog.uplog.domain.comment.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("comment_like")
public class CommentLike extends LikeBase {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commnet_id")
    private Comment comment;
}
