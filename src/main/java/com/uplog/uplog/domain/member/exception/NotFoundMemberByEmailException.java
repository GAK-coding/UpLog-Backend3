package com.uplog.uplog.domain.member.exception;

public class NotFoundMemberByEmailException extends IllegalArgumentException{
    public NotFoundMemberByEmailException(String m){super(m);}
    public NotFoundMemberByEmailException(){super("해당 이메일로 존재하는 객체를 찾을 수 없습니다.");}
}
