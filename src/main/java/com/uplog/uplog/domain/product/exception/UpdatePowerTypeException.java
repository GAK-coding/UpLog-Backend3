package com.uplog.uplog.domain.product.exception;

public class UpdatePowerTypeException extends IllegalArgumentException{
    public UpdatePowerTypeException(String m){super(m);}
    public UpdatePowerTypeException(){super("해당 권한으로 변경할 수 없습니다.");}
}
