package com.uplog.uplog.global.exception;

public class NotFoundIdException extends IllegalArgumentException{
    public NotFoundIdException(String m){ super(m); }
    public NotFoundIdException(){super("해당 ID로 존재하는 객체를 찾을 수 없습니다.");}
}
