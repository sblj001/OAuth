package com.yootk.util.interceptor;

import com.yootk.util.bean.BaseUriUtil;
import com.yootk.util.bean.MessageUtil;
import com.yootk.util.validate.ActionValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ValidationInterceptor implements HandlerInterceptor {
    @Autowired
    private MessageSource messageSource ;   // 直接注入资源信息读取Bean

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) { // 是否为HandleMethod类的实例
            HandlerMethod handlerMethod = (HandlerMethod) handler ; // 获取HandleMethod对象实例
            String validationKey = handlerMethod.getBeanType().getName() + "." + handlerMethod.getMethod().getName() ;
            String validationRules = MessageUtil.getMessage(validationKey,this.messageSource); // 保存具体的验证规则项
            if (validationRules == null || "".equals(validationRules)) {
                return true ;   // 不需要进行具体的验证处理
            } else {
                // 实例化具体的验证规则的处理类，实例化该类对象将自动进行规则的验证处理
                ActionValidationUtil avu = new ActionValidationUtil(request, this.messageSource, validationRules);
                if (avu.getErrors().size() > 0) {   // 此时产生了具体的错误信息
                    request.setAttribute("errors",avu.getErrors()); // 保存错误信息
                    String errorPage = MessageUtil.getMessage(validationKey + ".error.page",this.messageSource) ;
                    if (errorPage == null || "".equals(errorPage)) {
                        errorPage = MessageUtil.getMessage("error.page",this.messageSource) ;
                    }
                    if ("POST".equalsIgnoreCase(request.getMethod())) { // 判断请求模式
                        response.sendRedirect(BaseUriUtil.getBasePath() + errorPage);
                    } else {
                        request.getRequestDispatcher(errorPage).forward(request, response);
                    }
                    return false ; // 后续的Action不应该执行了
                } else {    // 当前的代码没有任何的错误
                    return true ; // 向后继续执行
                }
            }
        }
        return true;
    }
}