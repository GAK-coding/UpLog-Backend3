package com.uplog.uplog.domain.comment.exception;

public class NotFoundCommentByPostException extends IllegalArgumentException {

    public NotFoundCommentByPostException(String m){super(m);}
    public NotFoundCommentByPostException(Long PostId){super("Id : "+PostId+" 게시글에 대한 댓글을 찾을 수가 없습니다.");}
}
