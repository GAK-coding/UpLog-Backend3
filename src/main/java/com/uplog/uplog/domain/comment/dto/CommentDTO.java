package com.uplog.uplog.domain.comment.dto;

import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {



    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCommentRequest {

        private Long parentId;
        private String content;

        public Comment toEntity(Member member, Comment comment,Post post) {
            return Comment.builder()
                    .content(content)
                    .parent(comment)
                    .post(post)
                    .member(member)
                    .build();
        }
        public Comment toCommentEntity(Member member,Post post) {
            return Comment.builder()
                    .content(content)
                    .post(post)
                    .member(member)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleCommentInfo {

        private Long id;
        private Long memberId;
        private Long parentId;
        private String content;
        private String name;
        //private String image;
        private String nickName;
        private LocalDateTime createTime;



    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCommentContent{


        private String content;




    }
}
