package com.uplog.uplog.domain.like.dto;

import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.like.model.CommentLike;
import com.uplog.uplog.domain.like.model.PostLike;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class LikeDTO {
    //댓글이든 포스트든 아이디값은 parth variable로 넘어올거임. -> 댓글 좋아요만
//    @Builder
//    @Getter
//    @AllArgsConstructor
//    @NoArgsConstructor
//    public static class CreateLikeRequest{
        //필요한 변수는 다 pathVariable에 있음.
//        public PostLike toPostLike(Member member, Post post){
//            return PostLike.builder()
//                    .member(member)
//                    .post(post)
//                    .build();
//        }
//
//        public CommentLike toCommentLike(Member member, Comment comment){
//            return CommentLike.builder()
//                    .member(member)
//                    .comment(comment)
//                    .build();
//        }
//    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class LikeInfoDTO{
        private Long id;
        private String memberNickname;
        private int cnt;
    }

}
