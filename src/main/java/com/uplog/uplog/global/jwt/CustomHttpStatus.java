package com.uplog.uplog.global.jwt;

public enum CustomHttpStatus {

    ACCESS_EXPIRED(409,"Expired Access Token"),
    NON_LOGIN(438,"No Token included"),
    INVALID_TOKEN(411,"Token with invalid value"),
    NON_SUPPORT_TOKEN(412,"Token not supported"),
    ILLEGAL_ARG_TOKEN(413,"Illegal or inappropriate Token"),
    REFRESH_EXPIRED(410,"Expired Refresh Token");


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
