package com.uplog.uplog.domain.task.exception.handler;

public class NotFoundTaskByIdException extends IllegalArgumentException{
    public NotFoundTaskByIdException(String m){ super(m);}
    public NotFoundTaskByIdException(){super("해당 ID의 task가 존재하지 않습니다.");}

}

