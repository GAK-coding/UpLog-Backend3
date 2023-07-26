package com.uplog.uplog.domain.product.exception;

public class DuplicatedProductNameException extends IllegalArgumentException{
    public DuplicatedProductNameException(String m){super(m);}
    public DuplicatedProductNameException(){super("제품 이름이 중복되었습니다.");}
}
