package com.uplog.uplog.global.exception;

public class DepthException extends IllegalArgumentException{
    public DepthException(String s){super(s);}
    public DepthException(){super("depth를 초과했습니다.");}
}
