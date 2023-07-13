package com.uplog.uplog.domain.member.exception;

public class DuplicatedMemberException extends IllegalArgumentException{
    public DuplicatedMemberException(String m){super(m);}
    public DuplicatedMemberException(){super("이미 존재하는 회원입니다.");}
}
