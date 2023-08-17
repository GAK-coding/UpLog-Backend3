package com.uplog.uplog.global.exception;

public class NotFoundMemberByTeamException extends IllegalArgumentException{
    public NotFoundMemberByTeamException(String m){ super(m); }
    public NotFoundMemberByTeamException(){super("해당 멤버는 해당 프로젝트에 존재하지 않습니다.");}
}