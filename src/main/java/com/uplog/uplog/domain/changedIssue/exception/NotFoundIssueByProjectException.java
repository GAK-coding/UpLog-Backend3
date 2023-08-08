package com.uplog.uplog.domain.changedIssue.exception;

public class NotFoundIssueByProjectException extends IllegalArgumentException {

    public NotFoundIssueByProjectException(String m){super(m);}
    public NotFoundIssueByProjectException(Long ProjectId){super("Id : "+ProjectId+"에 대한 상세이력을 찾을 수가 없습니다.");}
}
