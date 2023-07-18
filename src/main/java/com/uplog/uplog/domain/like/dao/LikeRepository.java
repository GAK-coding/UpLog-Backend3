package com.uplog.uplog.domain.like.dao;

import com.uplog.uplog.domain.like.model.LikeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<LikeBase, Long> {
}
