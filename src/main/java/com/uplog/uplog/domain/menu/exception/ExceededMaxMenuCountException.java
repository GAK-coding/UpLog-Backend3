package com.uplog.uplog.domain.menu.exception;

public class ExceededMaxMenuCountException extends RuntimeException {
    public ExceededMaxMenuCountException() {
        super("해당 프로젝트의 메뉴 개수가 최대 개수를 초과하였습니다.");
    }
}
