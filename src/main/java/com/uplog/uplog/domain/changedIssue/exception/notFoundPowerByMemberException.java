package com.uplog.uplog.domain.changedIssue.exception;

public class notFoundPowerByMemberException extends IllegalArgumentException {

    public notFoundPowerByMemberException(String m){super(m);}
    public notFoundPowerByMemberException(Long memberId){super("Id : "+memberId+"에 해당 되는 권한을 찾을 수가 없습니다.");}
}
