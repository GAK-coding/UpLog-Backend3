package com.uplog.uplog.global.exception;

public class NotFountTeamByProjectException extends IllegalArgumentException{
    public NotFountTeamByProjectException(String m){ super(m); }
    public NotFountTeamByProjectException(){super("해당 그룹은 현재 프로젝트소속이 아닙니다.");}
}