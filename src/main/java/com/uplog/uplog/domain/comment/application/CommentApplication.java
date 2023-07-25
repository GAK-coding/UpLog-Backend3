package com.uplog.uplog.domain.comment.application;

import com.uplog.uplog.domain.comment.dao.CommentRepository;
import com.uplog.uplog.domain.comment.dto.CommentDTO;
import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.global.Exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentApplication {



    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentDTO.CommentInfo createComment(CommentDTO.CommentInfo commentData,Long postId, Long memberId){

        //Post post=postRepository.findById(postId).orElseThrow(NotFoundIdException::new);
        Member member = memberRepository.findById(memberId).orElseThrow(NotFoundMemberByEmailException::new);

        //parentId가 null일 때 기본 정보만 저장.
        if(commentData.getParentId()==null && commentData.getChildId()==null){


            Comment comment=commentData.of(member,null,null);
            commentRepository.save(comment);

        }

        //ParentId가 존재할 때 부모 객체를 mapping && 기본 정보 저장.
        else {
                //parent만 존재
                Comment ParentComment=commentRepository.findById(commentData.getParentId()).orElseThrow(NotFoundIdException::new);
                Comment comment=commentData.of(member,ParentComment,null);
                commentRepository.save(comment);

            }

        return commentData;


    }
}
