package com.uplog.uplog.global.exception;

public class DuplicatedNameException extends IllegalArgumentException{
    public DuplicatedNameException(String m){super(m);}
    public DuplicatedNameException(){super("이름이 중복됩니다.");}
}
