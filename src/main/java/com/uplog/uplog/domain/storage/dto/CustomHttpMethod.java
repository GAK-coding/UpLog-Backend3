package com.uplog.uplog.domain.storage.dto;

public enum CustomHttpMethod {
    TEMP("TEMP");

    private final String method;

    CustomHttpMethod(String method) {
        this.method = method;
    }

    public String value() {
        return method;
    }
}
