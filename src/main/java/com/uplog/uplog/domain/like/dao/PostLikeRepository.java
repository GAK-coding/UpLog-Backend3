package com.uplog.uplog.domain.like.dao;

import com.uplog.uplog.domain.like.model.LikeBase;
import com.uplog.uplog.domain.like.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findById(Long id);
    List<PostLike> findPostLikeByPostId(Long id);
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);
    PostLike findPostLikeByMemberIdAndPostId(Long memberId, Long postId);
    int countByPostId(Long id);

}
