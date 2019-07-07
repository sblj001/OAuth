package com.yootk.oauth.filter.authc;

import com.yootk.oauth.filter.token.OAuthToken;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class OAuthAuthenticatingFilter extends AuthenticatingFilter {
    private String authcodeParam = "code" ; // 由OAuth返回地址的提供参数名称
    private String failureUrl ; // 定义一个认证失败的处理页面
    private boolean rememberme ; // 是否要记住我
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        // 如果现在要进行Token的创建，则肯定要使用自定义的OAuthToken程序类进行处理
        OAuthToken token = new OAuthToken() ;
        token.setAuthcode(request.getParameter(this.authcodeParam)); // 需要传入一个authcode信息
        token.setRememberMe(this.rememberme); // 进行用户状态的保存
        return token ;  // 返回一个可以使用的Token标记
    }
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 在此方法之中需要去考虑访问是否被拒绝的问题，如果没有进行合理的登录就需要被拒绝
        String error = request.getParameter("error") ; // 此处可以获取相应的错误信息
        if (!(error == null || "".equals(error))) { // 此时有错误的内容
            String errorDesc = request.getParameter("error_description") ; // 获取错误的详情
            // 如果出现了错误，则应该跳转到相应的错误页
            WebUtils.issueRedirect(request, response, this.failureUrl + "?error=" + error + "&error_description=" + errorDesc);
            return false ; // 后续的处理不再执行
        }
        Subject subject = super.getSubject(request,response) ; // 获取用户的Subject
        if (!subject.isAuthenticated()) {   // 此时用户现在未认证
            String code = request.getParameter(this.authcodeParam) ; // 获取数据信息
            if (code == null && "".equals(code)) {  // 表示此时请求错误
                super.saveRequestAndRedirectToLogin(request,response); // 跳转到登录页
                return true ;   // 执行跳转操作
            }
        }
        return super.executeLogin(request,response);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        super.issueSuccessRedirect(request,response);
        return false ;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        Subject subject = super.getSubject(request,response) ; // 获取用户Subject
        if (subject.isAuthenticated() || subject.isRemembered()) {  // 用户已经认证过了
            try {
                super.issueSuccessRedirect(request,response);
            } catch (Exception e1){}
        } else {
            try {
                WebUtils.issueRedirect(request,response,this.failureUrl);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false ;
    }

    public void setAuthcodeParam(String authcodeParam) {
        this.authcodeParam = authcodeParam;
    }

    public void setFailureUrl(String failureUrl) {
        this.failureUrl = failureUrl;
    }

    public void setRememberme(boolean rememberme) {
        this.rememberme = rememberme;
    }
}
