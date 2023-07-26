package com.uplog.uplog.domain.comment.dao;

import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {


    //List<Comment> findByPostId(Long postid);
    List<Comment> findByAuthorId(Long memberid);
}
