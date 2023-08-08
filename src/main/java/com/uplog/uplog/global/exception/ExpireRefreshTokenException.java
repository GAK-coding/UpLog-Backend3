package com.uplog.uplog.global.exception;

public class ExpireRefreshTokenException extends IllegalArgumentException {


    public ExpireRefreshTokenException(String m){ super(m); }
    public ExpireRefreshTokenException(){super("refreshToken_expiration");}
}
