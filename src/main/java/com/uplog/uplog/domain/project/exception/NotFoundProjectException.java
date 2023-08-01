package com.uplog.uplog.domain.project.exception;

public class NotFoundProjectException extends IllegalArgumentException {

    public NotFoundProjectException(String m){super(m);}
    public NotFoundProjectException(Long projectId){super("Id : "+projectId+"해당 프로젝트를 찾을 수가 없습니다.");}
}

