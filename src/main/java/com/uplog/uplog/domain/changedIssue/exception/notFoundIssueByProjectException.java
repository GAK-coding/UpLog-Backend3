package com.uplog.uplog.domain.changedIssue.exception;

public class notFoundIssueByProjectException extends IllegalArgumentException {

    public notFoundIssueByProjectException(String m){super(m);}
    public notFoundIssueByProjectException(Long ProjectId){super("Id : "+ProjectId+"에 대한 상세이력을 찾을 수가 없습니다.");}
}
