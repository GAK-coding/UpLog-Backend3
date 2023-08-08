package com.uplog.uplog.domain.comment.dto;

import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class CommentDTO {


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateComment {

        private Long id;
        private Post post;
        private Member member;
        private Comment parent;
        private List<Comment> childlist = new ArrayList<>();
        private String content;

        public Comment toEntity() {
            return Comment.builder()
                    .id(this.id)
                    .post(this.post)
                    .member(this.member)
                    .childList(this.childlist)
                    .content(this.content)
                    .build();

        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentInfo {

        private Long parentId;
        private List<Long> childId;
        private String content;

        public Comment toEntity(Member member, Comment comment, List<Comment> childList) {
            return Comment.builder()
                    .content(content)
                    .parent(comment)
                    .childList(childList)
                    .member(member)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadCommentInfo{

        private Long id;
        private Long memberId;
        private Long parentId;
        private String content;



    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCommentContent{


        private String content;




    }
}
