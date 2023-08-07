package com.uplog.uplog.global.exception;

import io.jsonwebtoken.ExpiredJwtException;

public class ExpireJwtTokenException extends IllegalArgumentException {


    public ExpireJwtTokenException(String m){ super(m); }
    public ExpireJwtTokenException(){super("만료된 JWT 토큰입니다.");}
}
