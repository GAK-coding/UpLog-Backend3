package com.uplog.uplog.domain.comment.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uplog.uplog.domain.comment.dao.CommentRepository;
import com.uplog.uplog.domain.comment.dto.CommentDTO.CreateCommentRequest;
import com.uplog.uplog.domain.comment.exception.MemberAuthorizedException;
import com.uplog.uplog.domain.comment.exception.NotFoundCommentByPostException;
import com.uplog.uplog.domain.comment.exception.NotFoundCommentException;
import com.uplog.uplog.domain.comment.model.Comment;
import com.uplog.uplog.domain.comment.model.QComment;
import com.uplog.uplog.domain.member.dao.MemberRepository;
import com.uplog.uplog.domain.member.exception.NotFoundMemberByEmailException;
import com.uplog.uplog.domain.member.model.Member;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.post.model.Post;
import com.uplog.uplog.global.exception.NotFoundIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import static com.uplog.uplog.domain.comment.dto.CommentDTO.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentApplication {

    @PersistenceContext
    private EntityManager entityManager;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

     /*
        CREATE
     */
    @Transactional
    public SimpleCommentInfo createComment(CreateCommentRequest commentData, Long postId, Long memberId){

        Post post=postRepository.findById(postId).orElseThrow(NotFoundIdException::new);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(NotFoundMemberByEmailException::new);

        SimpleCommentInfo simpleCommentInfo;
        //parentId가 null일 때 기본 정보만 저장.
        if(commentData.getParentId()==null && commentData.getChildId()==null){


            Comment comment=commentData.toEntity(member,null,null,post);


            commentRepository.save(comment);
            simpleCommentInfo=comment.toSimpleCommentInfo();
        }

        //ParentId가 존재할 때 부모 객체를 mapping && 기본 정보 저장.
        else {
                //parent만 존재
                Comment ParentComment=commentRepository.findById(commentData.getParentId())
                        .orElseThrow(()->new NotFoundCommentException(commentData.getParentId()));
                Comment comment=commentData.toEntity(member,ParentComment,null,post);

                commentRepository.save(comment);

                simpleCommentInfo=comment.toSimpleCommentInfo();

            }

        return simpleCommentInfo;


    }


     /*
        READ
     */

    @Transactional(readOnly = true)
    public List<SimpleCommentInfo> findCommentByPostId(Long postId){

        // TODO: post 만들어지면 post로 바꿔야함.
        List<Comment> commentList=commentRepository.findByAuthorId(postId);

        if(commentList==null||commentList.isEmpty()){
            throw new NotFoundCommentByPostException(postId);
        }

        List<SimpleCommentInfo> commentInfos=new ArrayList<>();

        for(Comment comment_tmp : commentList)
            commentInfos.add(comment_tmp.toSimpleCommentInfo());

        return commentInfos;



    }

    /*
     멘션에서 눌렀을 때 해당 댓글만 따로 보여주거나 대댓글일 때는 댓글과 대댓글만 보여주는
     기능이 있으면 좋을 것 같다는 생각에 넣어봄
    */
    @Transactional(readOnly = true)
    public List<SimpleCommentInfo> findCommentById(Long commentId){

        List<SimpleCommentInfo> simpleCommentInfos =new ArrayList<>();
        Comment comment=commentRepository.findById(commentId)
                .orElseThrow(()->new NotFoundCommentException(commentId));


        //대댓글일 시 그의 부모 댓글과 함께 조회
        if(comment.getParent()!=null){
            Comment comment_parent=commentRepository.findById(comment.getParent().getId())
                    .orElseThrow(()->new NotFoundCommentException(comment.getParent().getId()));
            simpleCommentInfos.add(comment_parent.toSimpleCommentInfo());

        }
        simpleCommentInfos.add(comment.toSimpleCommentInfo());

        return simpleCommentInfos;

    }

     /*
        UPDATE
     */

    @Transactional
    public SimpleCommentInfo updateCommentContent(UpdateCommentContent updateCommentContent, Long commentId, Long memberId){

        //업데이트 사항은 content 단일 항목이고 업데이트 후 해당 comment의 정보를 모두 넘겨줌
        Comment comment=commentRepository.findById(commentId)
                .orElseThrow(()->new NotFoundCommentException(commentId));

        if(!memberValidate(comment.getAuthor().getId(),memberId)){
            throw new NotFoundIdException();
        }

        comment.updateCommentContent(updateCommentContent.getContent());
        SimpleCommentInfo simpleCommentInfo =comment.toSimpleCommentInfo();
        return simpleCommentInfo;



    }

     /*
        DELETE
     */

    @Transactional
    public String deleteComment(Long commentId, Long memberId){


        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        QComment comment= QComment.comment;

        //삭제 된 comment들 id도 넘겨주면 좋으려나?
        Comment comment_sgl=query
                .selectFrom(comment)
                .where(comment.id.eq(commentId))
                .fetchOne();

        if(comment_sgl==null){
            throw new NotFoundCommentException(commentId);
        }

        if(!memberValidate(comment_sgl.getAuthor().getId(),memberId)){
            throw new MemberAuthorizedException(memberId);
        }

        //Comment comment=commentRepository.findById(commentId).orElseThrow(NotFoundIdException::new);
        commentRepository.delete(comment_sgl);
        return "DELETE OK";
    }

     /*
        VALIDATE
     */

    public boolean memberValidate(Long CommentMemberId, Long currMemberId ){

        return (CommentMemberId==currMemberId)?true:false;

    }


}
