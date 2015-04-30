package com.weiqi.weaver.http;

public enum Method {
    OPTIONS("OPTIONS"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    TRACE("TRACE"),
    CONNECT("CONNECT");
    
    private final String value;
    
    private Method(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
