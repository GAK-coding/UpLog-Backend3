package com.uplog.uplog.domain.comment.application;

import com.uplog.uplog.domain.comment.dao.CommentRepository;
import com.uplog.uplog.domain.comment.dto.CommentDTO;
import com.uplog.uplog.domain.comment.dto.CommentDTO.CommentInfo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.uplog.uplog.domain.comment.dto.CommentDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentApplication {



    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

     /*
        CREATE
     */
    @Transactional
    public CommentInfo createComment(CommentInfo commentData, Long postId, Long memberId){

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


     /*
        READ
     */

    public List<ReadCommentInfo> ReadPostComment(Long postId){

        //post 만들어지면 post로 바꿔야함.
        List<Comment> commentList=commentRepository.findByAuthorId(postId);

        if(commentList==null||commentList.isEmpty()){
            throw new NotFoundIdException();
        }

        List<ReadCommentInfo> commentInfos=new ArrayList<>();

        for(Comment comment_tmp : commentList)
            commentInfos.add(comment_tmp.of());

        return commentInfos;



    }

     /*
        UPDATE
     */

    @Transactional
    public ReadCommentInfo UpdateCommentContent(UpdateCommentContent updateCommentContent,Long commentId){

        //업데이트 사항은 content 단일 항목이고 업데이트 후 해당 comment의 정보를 모두 넘겨줌
        Comment comment=commentRepository.findById(commentId).orElseThrow(NotFoundIdException::new);
        comment.UpdateCommentContent(updateCommentContent.getContent());
        ReadCommentInfo readCommentInfo=comment.of();
        return readCommentInfo;



    }

     /*
        DELETE
     */

    @Transactional
    public String DeleteComment(Long commentId){

        //삭제 된 comment들 id도 넘겨주면 좋으려나?
        Comment comment=commentRepository.findById(commentId).orElseThrow(NotFoundIdException::new);
        commentRepository.delete(comment);
        return "DELETE OK";
    }


}
