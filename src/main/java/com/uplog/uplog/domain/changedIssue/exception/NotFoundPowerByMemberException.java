package com.uplog.uplog.domain.changedIssue.exception;

public class NotFoundPowerByMemberException extends IllegalArgumentException {

    public NotFoundPowerByMemberException(String m){super(m);}
    public NotFoundPowerByMemberException(Long memberId){super("Id : "+memberId+"에 해당 되는 권한을 찾을 수가 없습니다.");}
}
