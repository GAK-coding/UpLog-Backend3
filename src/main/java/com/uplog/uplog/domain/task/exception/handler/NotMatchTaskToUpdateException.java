package com.uplog.uplog.domain.task.exception.handler;

public class NotMatchTaskToUpdateException extends IllegalArgumentException{
    public NotMatchTaskToUpdateException(String m){ super(m);}
    public NotMatchTaskToUpdateException(){super("수정하려는 task와 일치하지 않습니다.");}
}