package com.uplog.uplog.domain.tag.dao;

import com.uplog.uplog.domain.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByContent(String content);
    Optional<Tag> findById(Long id);


}
