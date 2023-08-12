package com.uplog.uplog.domain.product.exception;

public class MasterException extends IllegalArgumentException{
    public MasterException(String m){super(m);}
    public MasterException(){super("마스터는 한명만 존재할 수 있습니다.");}
}
