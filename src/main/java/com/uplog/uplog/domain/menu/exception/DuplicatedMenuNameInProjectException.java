package com.uplog.uplog.domain.menu.exception;

public class DuplicatedMenuNameInProjectException extends IllegalArgumentException{
    public DuplicatedMenuNameInProjectException(String m){super(m);}
    public DuplicatedMenuNameInProjectException(){super("해당 프로젝트내에서 메뉴 이름이 중복됩니다.");}
}
