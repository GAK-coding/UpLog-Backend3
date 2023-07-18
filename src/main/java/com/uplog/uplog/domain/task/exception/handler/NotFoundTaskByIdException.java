package com.uplog.uplog.domain.task.exception.handler;

public class NotFoundTaskByIdException extends Exception {
    public NotFoundTaskByIdException(String m) {
        super(m);
    }

    public NotFoundTaskByIdException() {
        super("해당 id를 가진 task는 존재하지 않습니다");
    }
}
