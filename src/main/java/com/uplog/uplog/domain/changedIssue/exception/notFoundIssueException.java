package com.uplog.uplog.domain.changedIssue.exception;

public class notFoundIssueException extends IllegalArgumentException{

        public notFoundIssueException(String m){super(m);}
        public notFoundIssueException(Long IssueId){super("Id : "+IssueId+"해당 상세이력을 찾을 수가 없습니다.");}
}
