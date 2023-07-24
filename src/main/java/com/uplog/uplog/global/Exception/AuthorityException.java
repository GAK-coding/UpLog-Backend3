package com.uplog.uplog.global.Exception;

public class AuthorityException extends IllegalArgumentException{
    public AuthorityException(String m){super(m);}
    public AuthorityException(){super("권한이 없습니다.");}
}
