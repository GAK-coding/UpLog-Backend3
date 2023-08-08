package com.uplog.uplog.domain.comment.exception;

public class MemberAuthorizedException extends IllegalArgumentException{

    public MemberAuthorizedException(String m){super(m);}
    public MemberAuthorizedException(Long memberId){super("Id : "+memberId+" 해당 게시글에 대한 수정,삭제 권한이 없는 사용자 입니다.");}
}
