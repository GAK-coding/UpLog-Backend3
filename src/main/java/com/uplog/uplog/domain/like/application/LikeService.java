package com.uplog.uplog.domain.like.application;

import com.uplog.uplog.domain.comment.dao.CommentRepository;
import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.like.dao.CommentLikeRepository;
//import com.uplog.uplog.domain.like.dao.LikeBaseRepository;
import com.uplog.uplog.domain.like.dao.PostLikeRepository;
import com.uplog.uplog.domain.like.dto.LikeDTO;
import com.uplog.uplog.domain.like.dto.LikeDTO.LikeInfoDTO;
import com.uplog.uplog.domain.like.model.CommentLike;
import com.uplog.uplog.domain.like.model.PostLike;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final PostLikeRepository postLikeRepository;
    //private final LikeBaseRepository likeBaseRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public LikeInfoDTO createPostLike(Long memberId, Long postId) {
        Member member = memberRepository.findMemberById(memberId).orElseThrow(NotFoundIdException::new);
        Post post = postRepository.findById(postId).orElseThrow(NotFoundIdException::new);

        if (postLikeRepository.existsByMemberIdAndPostId(memberId, postId)) {
            PostLike postLike = postLikeRepository.findPostLikeByMemberIdAndPostId(memberId, postId);
            postLikeRepository.delete(postLike);
            int cnt = postLikeRepository.countByPostId(postId);
            return LikeInfoDTO.builder()
                    .id(postLike.getId())
                    .memberNickname(member.getNickname())
                    .cnt(cnt)
                    .build();
        } else {
            PostLike postLike = PostLike.builder()
                    .member(member)
                    .post(post)
                    .build();
            postLikeRepository.save(postLike);
            int cnt = postLikeRepository.countByPostId(postId);
            return LikeInfoDTO.builder()
                    .id(postLike.getId())
                    .memberNickname(member.getNickname())
                    .cnt(cnt)
                    .build();
        }
    }

    @Transactional
    public LikeInfoDTO createCommentLike(Long memberId, Long commentId) {
        Member member = memberRepository.findMemberById(memberId).orElseThrow(NotFoundIdException::new);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NotFoundIdException::new);

        if (commentLikeRepository.existsByMemberIdAndCommentId(memberId, commentId)) {
            CommentLike commentLike = commentLikeRepository.findCommentLikesByMemberIdAndCommentId(memberId, commentId);
            commentLikeRepository.delete(commentLike);
            int cnt = commentLikeRepository.countByCommentId(commentId);


            return LikeInfoDTO.builder()
                    .id(commentLike.getId())
                    .memberNickname(member.getNickname())
                    .cnt(cnt)
                    .build();
        } else {
            CommentLike commentLike = CommentLike.builder()
                    .member(member)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(commentLike);
            int cnt = commentLikeRepository.countByCommentId(commentId);


            return LikeInfoDTO.builder()
                    .id(commentLike.getId())
                    .memberNickname(member.getNickname())
                    .cnt(cnt)
                    .build();
        }

    }

    //===================조회=============================
    //객수만 조회됨
    //TODO 나중에 좋아요 누른 사람 조회도 됐으면 좋겠어용
    public int countPostLike(Long postId){
        return postLikeRepository.countByPostId(postId);
    }

    public int countCommentLike(Long commentId){
        return commentLikeRepository.countByCommentId(commentId);
    }

    //멤버별로 좋아요 누른 사람 조회

    //조회
//    @Transactional(readOnly = true)
//    public int test(Long postId){
//        return likeBaseRepository.countLikeBaseByPostId(postId);
//    }

}
