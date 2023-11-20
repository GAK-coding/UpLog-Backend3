package com.uplog.uplog.domain.team.exception;

public class CanNotDeleteTeamException extends IllegalArgumentException{
    public CanNotDeleteTeamException(String s){super(s);}
    public CanNotDeleteTeamException(){super("팀을 삭제할 수 없습니다.");}
}
