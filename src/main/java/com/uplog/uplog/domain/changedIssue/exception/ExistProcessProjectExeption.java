package com.uplog.uplog.domain.changedIssue.exception;

public class ExistProcessProjectExeption extends IllegalArgumentException {

    public ExistProcessProjectExeption(String m){super(m);}
    public ExistProcessProjectExeption(Long ProjectId){super("Id : "+ProjectId+"가 진행 중입니다. 진행을 완료 해주세요 ");}
}
