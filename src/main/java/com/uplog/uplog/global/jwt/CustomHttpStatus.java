package com.uplog.uplog.global.jwt;

public enum CustomHttpStatus {

    ACCESS_EXPIRED(409),REFRESH_EXPIRED(410);

    private final int value;

    private CustomHttpStatus(int value){
        this.value=value;
    }

    public int value(){
        return this.value;
    }
}
