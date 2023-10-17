package com.uplog.uplog.global.jwt;

public enum CustomHttpStatus {

    ACCESS_EXPIRED(409,"Expired Access Token"),REFRESH_EXPIRED(410,"Expired Refresh Token");

    private final int status;
    private final String description;

    private CustomHttpStatus(int status, String description){
        this.status = status;
        this.description = description;
    }

    public int getStatus(){
        return this.status;
    }

    public String getDescription(){
        return this.description;
    }

}
