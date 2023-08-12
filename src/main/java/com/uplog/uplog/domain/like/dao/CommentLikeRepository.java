package com.uplog.uplog.domain.like.dao;

import com.uplog.uplog.domain.like.model.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findById(Long id);
    List<CommentLike> findCommentLikesByMemberId(Long memberId);
    List<CommentLike> findCommentLikesByCommentId(Long commentId);
    boolean existsByMemberIdAndCommentId(Long memberId, Long commentId);
    CommentLike findCommentLikesByMemberIdAndCommentId(Long memberId, Long commentId);
    int countByCommentId(Long commentId);
}
