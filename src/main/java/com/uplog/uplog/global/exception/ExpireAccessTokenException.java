package com.uplog.uplog.global.exception;

public class ExpireAccessTokenException extends RuntimeException {


    public ExpireAccessTokenException(String m){ super(m); }
    public ExpireAccessTokenException(){super("access_expiration");}
}
