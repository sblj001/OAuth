package com.yootk.oauth.filter.token;

import org.apache.shiro.authc.UsernamePasswordToken;

public class OAuthToken extends UsernamePasswordToken {
    private Object principal ;  // 保存用户名
    private String authcode ;   // 保存code数据
    @Override
    public Object getPrincipal() {
        return principal;
    }
    public void setPrincipal(Object principal) {
        this.principal = principal;
    }
    @Override
    public Object getCredentials() {
        return this.authcode; // 以往都是输入的密码，现在设置为了authcode
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }
}
