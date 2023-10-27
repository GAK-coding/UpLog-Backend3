package com.uplog.uplog.domain.team.exception;

import com.uplog.uplog.global.exception.NotFoundIdException;

public class NotFoundMemberInTeamException extends IllegalArgumentException{
    public NotFoundMemberInTeamException(String s){super(s);}
    public NotFoundMemberInTeamException(){super("팀에 존재하지 않는 멤버입니다.");}
}

