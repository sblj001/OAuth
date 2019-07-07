package com.yootk.oauth.filter.authc.exception;

import org.apache.shiro.authc.AuthenticationException;

public class RandomCodeException extends AuthenticationException {
    public RandomCodeException() {}
    public RandomCodeException(String msg) {
        super(msg) ;
    }
}
