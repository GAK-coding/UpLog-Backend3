package com.uplog.uplog.domain.team.exception;

public class CanNotDeleteMemberToTeam extends IllegalArgumentException{
    public CanNotDeleteMemberToTeam(String s){super(s);}
    public CanNotDeleteMemberToTeam(){super("팀에서 퇴장할 수 없습니다.");}
}
