package com.uplog.uplog.domain.project.exception;

import com.uplog.uplog.domain.project.model.ProjectStatus;

public class DuplicateVersionNameException extends IllegalArgumentException {

    public DuplicateVersionNameException(String ProjectId){super("Id : "+ProjectId+"는 중복 되는 프로젝트 이름입니다. ");}

}
