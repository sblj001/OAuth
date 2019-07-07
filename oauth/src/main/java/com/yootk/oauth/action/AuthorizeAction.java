package com.yootk.oauth.action;

import com.yootk.oauth.service.IOAuthService;
import com.yootk.oauth.util.cache.RedisCache;
import com.yootk.vo.Client;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@Controller
@PropertySource("classpath:config/oauth.properties")
public class AuthorizeAction {
    public static final String AUTHCODE_CACHE_NAME = "authcodeCache" ;
    @Value("${oauth.authcode.expire}")
    private long expire ;
    @Autowired
    private IOAuthService oauthService;
    private RedisCache<Object,Object> authcodeRedisCache ;
    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        this.authcodeRedisCache = (RedisCache<Object, Object>) cacheManager.getCache(AUTHCODE_CACHE_NAME);
    }

    @ResponseBody
    @RequestMapping(value = "/oauth2/authorize", method = RequestMethod.GET)
    public Object authcode(HttpServletRequest request) {
        try {// OAuth本身属于一个处理标准，所以对于标准的操作就必须采用其规定的处理流程来完成
            // 1、如果要想进行oAuth处理则必须将请求转换为OAuth请求
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            // 2、对于AuthCode的生成一定要接收clientId的数据信息
            String clientId = oauthRequest.getClientId();
            // 3、那么此时就需要判断给定的clientId的内容是否合法，这个合法就可以依靠RPC来进行检测
            Client client = this.oauthService.get(clientId); // 分布式调用
            if (client == null) {   // 此时client_id无效
                OAuthResponse authResponse = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription("无效的Client_ID，请确认传输正确！")
                        .buildJSONMessage();// 将数据以JSON的形式返回
                return new ResponseEntity<String>(authResponse.getBody(), HttpStatus.valueOf(authResponse.getResponseStatus()));
            }
            Subject subject = SecurityUtils.getSubject(); // 获取当前的用户操作Subject
            if (!subject.isAuthenticated()) {   // 如果此时用户没有进行过认证
                WebUtils.saveRequest(request); // 保留当前的request对象
                HttpHeaders headers = new HttpHeaders(); // 创建头信息
                headers.setLocation(new URI(request.getContextPath() + "/login.action"));
                return new ResponseEntity<String>(headers, HttpStatus.TEMPORARY_REDIRECT);
            }
            String authcode = null; // client_id合法就需要生成authcode
            // 获取当前操作的响应类型，这个类型是定义OAuth连接时规定好的参数
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            // 4、如果此时响应的类型为code，那么就表示可以生成authcode
            if (ResponseType.CODE.toString().equalsIgnoreCase(responseType)) {  // 类型匹配
                // 5、定义一个用于分配验证码的处理程序类，这个类需要设置一个加密器
                OAuthIssuerImpl authIssuer = new OAuthIssuerImpl(new MD5Generator());
                authcode = authIssuer.authorizationCode(); // 生成authcode
                this.authcodeRedisCache.putEx(authcode,subject.getPrincipal(),this.expire) ;
            }
            // 6、如果此时clientID正确了，同时也可以生成了authcode，那么就需要把这个authcode的内容返回给客户端
            // 7、构建一个OAuth的回应的访问路径
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);
            builder.setCode(authcode); // 将生成的authcode的内容发送回给客户端
            String redirectUri = oauthRequest.getRedirectURI(); // 获取回应的跳转地址
            // 依据此路径要构建一个返回的路径信息，而这个返回的路径信息里面一定要包含有code数据。
            OAuthResponse oauthResponse = builder.location(redirectUri).buildQueryMessage();
            HttpHeaders headers = new HttpHeaders(); // 设置回应的头信息内容
            headers.setLocation(new URI(oauthResponse.getLocationUri())); // 设置头信息的跳转
            return new ResponseEntity<String>(headers, HttpStatus.valueOf(oauthResponse.getResponseStatus()));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("服务器内部错误，请稍后重试！", HttpStatus.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));    // 响应成功
        }
    }
}
