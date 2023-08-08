package com.uplog.uplog.domain.changedIssue.exception;

public class NotFoundIssueException extends IllegalArgumentException{

        public NotFoundIssueException(String m){super(m);}
        public NotFoundIssueException(Long IssueId){super("Id : "+IssueId+"해당 상세이력을 찾을 수가 없습니다.");}
}
