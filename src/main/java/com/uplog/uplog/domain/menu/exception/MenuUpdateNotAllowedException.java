package com.uplog.uplog.domain.menu.exception;

public class MenuUpdateNotAllowedException extends IllegalArgumentException{
    public MenuUpdateNotAllowedException(String m){super(m);}
    public MenuUpdateNotAllowedException(){super("해당 메뉴는 변경할 수 없습니다.");}
}
