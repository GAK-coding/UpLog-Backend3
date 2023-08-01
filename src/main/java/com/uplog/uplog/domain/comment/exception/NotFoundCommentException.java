package com.uplog.uplog.domain.comment.exception;

public class NotFoundCommentException extends IllegalArgumentException  {

    public NotFoundCommentException(String m){super(m);}
    public NotFoundCommentException(Long commentId){super("Id : "+commentId+"해당 게시글을 찾을 수가 없습니다.");}
}
