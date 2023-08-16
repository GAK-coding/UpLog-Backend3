package com.uplog.uplog.domain.tag.dao;

import com.uplog.uplog.domain.tag.model.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

}

