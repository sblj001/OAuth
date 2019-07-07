package com.yootk.oauth.filter.authc;

import com.yootk.oauth.util.cache.RedisCache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

public class KickoutSessionControlFilter extends AccessControlFilter {
    private String kickoutUrl ; // 设置有一个用户被剔除之后的跳转路径
    // kickoutAfter = true：剔除之后的（后登录的自动剔除）；
    // kickoutAfter = false：剔除之前登录的（前面的用户session失效）
    private boolean kickoutAfter = false ; // 是之前剔除还是之后剔除
    private int max = 1 ; // 设置在线访问的最大并发量
    private RedisCache<Object,Object> kickoutCache ; // 获取专属的用户在线资源的Redis连接
    private String kickoutCacheName ; // 设置缓存的名称
    private CacheManager cacheManager ;
    private String kickoutAttributeName = "kickout" ; // 剔除的属性名称
    private SessionManager sessionManager ; // Session管理
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;   // 该操作必须返回false，否则不会执行“onAccessDenied()”
    }
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 剔除的本质是对session的控制，所以获取Subject是核心，Subject可以获取session、用户id
        Subject subject = super.getSubject(request,response) ;
        if (!subject.isAuthenticated() && !subject.isRemembered()) {    // 此时没有认证
            return true ;// 执行后续的其它过滤操作
        }
        // 如果此时用户已经登录过了，那么现在就必须要针对于用户的session进行有效的处理控制
        Session session = subject.getSession() ; // 获取当前用户的Session对象
        // 所有的session数据最终一定要保存在Redis之中，Redis存储的key为mid、value为Deque
        String mid =  (String) subject.getPrincipal() ; // 获取用户id
        // 获取当前用户下对应的所有Session对象的队列集合（队列中保存的是SessionID）
        Deque<Serializable> allSessions = (Deque<Serializable>) this.kickoutCache.get(mid) ;    // 获取保存过双端队列
        if (allSessions == null) {  // 此时没有队列存储
            allSessions = new LinkedList<>() ; // 实例化双端队列接口对象
        }
        // 需要判断当前的session是否已经存在于集合之中，如果当前的sessionId不在，则意味着用户可以进行存储
        if (!allSessions.contains(session.getId()) &&
                session.getAttribute(this.kickoutAttributeName) == null) {
            allSessions.push(session.getId()); // 将sessionId存储在队列之中
            this.kickoutCache.put(mid,allSessions) ; // 将session队列数据保存在Redis之中
        }
        try {
            if (allSessions.size() > this.max) {    // 保存的sessionId数量大于最大并发量
                Serializable kickoutSessionId = null; // 保存要剔除的SessionId的数据
                if (this.kickoutAfter == true) {    // 如果剔除的是后者
                    kickoutSessionId = allSessions.removeFirst(); // 剔除前者
                } else {
                    kickoutSessionId = allSessions.removeLast(); // 剔除后者
                }
                this.kickoutCache.put(mid, allSessions); // Redis集合保存新的队列信息
                // 那么此时如果当前的用户还在访问，一定可以获取到kickoutSessionId数据
                // 通过Session管理器，根据指定的sessionId获取对应的Session对象数据
                Session kickoutSession = this.sessionManager.getSession(
                        new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {   // 该用户现在还在系统中访问
                    kickoutSession.setAttribute(this.kickoutAttributeName, true); // 剔除标记
                }
            }
        } catch (Exception e) {}
        // 所有的Session都会执行此过滤器，于是那一个Session存在有指定名称的属性内容，则表示该session要被注销
        if (session.getAttribute(this.kickoutAttributeName) != null) {
            subject.logout(); // 强制性离开
            super.saveRequest(request); // 记录下用户当前的请求状态
            // 进行路径的跳转处理
            WebUtils.issueRedirect(request,response,this.kickoutUrl + "?kickmsg=out");
            return false ; // 不再进行后续的信息访问
        }
        return true;    // 如果正确则执行后续的服务处理
    }
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.kickoutCache = (RedisCache<Object, Object>) this.cacheManager.getCache(this.kickoutCacheName) ;
    }
    public void setKickoutCacheName(String kickoutCacheName) {
        this.kickoutCacheName = kickoutCacheName;
    }
    public void setMax(int max) {
        this.max = max;
    }
    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }
    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }
    public void setKickoutAttributeName(String kickoutAttributeName) {
        this.kickoutAttributeName = kickoutAttributeName;
    }
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
