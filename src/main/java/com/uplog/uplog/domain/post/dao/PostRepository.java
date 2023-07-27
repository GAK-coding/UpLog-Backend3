package com.uplog.uplog.domain.post.dao;

import com.uplog.uplog.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);
    List<Post> findByMenuId(Long menuId);
}
