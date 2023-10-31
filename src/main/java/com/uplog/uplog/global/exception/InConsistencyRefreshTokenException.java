package com.uplog.uplog.global.exception;

public class InConsistencyRefreshTokenException extends IllegalArgumentException {


    public InConsistencyRefreshTokenException(String m){ super(m); }
    public InConsistencyRefreshTokenException(){super("refreshToken_Inconsistency");}
}

