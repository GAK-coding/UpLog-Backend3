package com.uplog.uplog.member.exception;

public class NotMatchPasswordException extends IllegalArgumentException{
    public NotMatchPasswordException(String m){super(m);}
    public NotMatchPasswordException(){super("비밀번호가 일치하지 않습니다.");}
}
