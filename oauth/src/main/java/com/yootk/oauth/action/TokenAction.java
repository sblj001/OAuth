package com.yootk.oauth.action;

import com.yootk.oauth.service.IOAuthService;
import com.yootk.oauth.util.cache.RedisCache;
import com.yootk.vo.Client;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Controller
@PropertySource("classpath:config/oauth.properties")
public class TokenAction {
    public static final String TOKEN_CACHE_NAME = "tokenCache" ;
    public static final String AUTHCODE_CACHE_NAME = "authcodeCache" ;
    @Value("${oauth.token.expire}")
    private long expire ;
    @Autowired
    private IOAuthService oauthService ;
    private RedisCache<Object, Object> tokenRedisCache;
    private RedisCache<Object, Object> authcodeRedisCache ;
    @Autowired
    public void setCacheManager(CacheManager cacheManager) {
        this.tokenRedisCache = (RedisCache<Object, Object>) cacheManager.getCache(TOKEN_CACHE_NAME);
        this.authcodeRedisCache = (RedisCache<Object, Object>) cacheManager.getCache(AUTHCODE_CACHE_NAME);
    }
    @ResponseBody
    @RequestMapping(value = "/oauth2/accessToken",method = RequestMethod.POST)
    public Object accessToken(HttpServletRequest request) {
        try {// 对于Token生成，在OAuth之中已经明确要求必须基于POST请求模式
            // 1、在整个的程序之中，如果要想进行Token数据的生成处理，那么最为重要的内容就是要想办法获取用户的mid的信息
            // 对于此时的mid的数据信息是可以直接利用authcode信息获取的，如果没有找到对应的authcode那么就表示当前的信息不存在
            OAuthTokenRequest tokenRequest = new OAuthTokenRequest(request);// 根据请求创建OAuth请求
            Object mid = null ; // 利用mid的信息进行数据的存储，主要是利用其来判断是否存在有特定的authcode
            // 2、必须判断当前用户的请求是否与当前指定的请求的授权类型相匹配
            if (tokenRequest.getParam(OAuth.OAUTH_GRANT_TYPE)
                    .equals(GrantType.AUTHORIZATION_CODE.toString())) {
                String authcode = tokenRequest.getParam(OAuth.OAUTH_CODE) ; // 获取authcode
                try {
                    mid = this.authcodeRedisCache.get(authcode); // 根据authcode获取对应的mid数据
                } catch (Exception e) {}    // 出现了错误就表示没有对应的authcode内容存储
            }
            // 3、如果此时没有查询到mid，那么就表示用户发送的授权码出现了错误（超时了）
            if (mid == null) {  // 查不到对应的mid数据，则认为authcode不合法
                OAuthResponse authResponse = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_GRANT)
                        .setErrorDescription("无效的授权码！")
                        .buildJSONMessage();// 将数据以JSON的形式返回
                return new ResponseEntity<String>(authResponse.getBody(), HttpStatus.valueOf(authResponse.getResponseStatus()));
            }
            // 4、如果此时可以查找到mid的数据，则认为当前的authcode有效，则需要获取客户的信息
            String clientId = tokenRequest.getClientId() ; // 获取客户信息
            // 5、当authcode合法之后，要根据clientId获取对应的Client对象信息
            Client client = this.oauthService.get(clientId);// 根据客户的id查询客户完整对象
            if (client == null) {   // 此客户id已经不存在了
                OAuthResponse authResponse = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription("无效的Client_ID，请确认传输正确！")
                        .buildJSONMessage();// 将数据以JSON的形式返回
                return new ResponseEntity<String>(authResponse.getBody(), HttpStatus.valueOf(authResponse.getResponseStatus()));
            }
            // 6、除了clientId验证之外，还需要验证ClientSecret
            String clientSecret = tokenRequest.getClientSecret() ;
            if (!client.getClientSecret().equals(clientSecret)) {   // 密码错误
                OAuthResponse authResponse = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription("无效的Client_Secret，请确认传输正确！")
                        .buildJSONMessage();// 将数据以JSON的形式返回
                return new ResponseEntity<String>(authResponse.getBody(), HttpStatus.valueOf(authResponse.getResponseStatus()));
            }
            // 7、如果此时的客户信息有效，那么就可以依据客户信息生成一个accessToken，定义生成器
            OAuthIssuerImpl oauthIssuer = new OAuthIssuerImpl(new MD5Generator());
            String accessToken = oauthIssuer.accessToken() ; // 生成AccessToken
            // 8、因为Token是交互的主要操作项，所以此时应该考虑将Token保存在Redis数据库之中
            this.tokenRedisCache.putEx(accessToken,mid, TimeUnit.DAYS,this.expire) ;
            // 9、将生成的Token的数据发送给客户端，创建一个新的响应处理，返回的内容为JSON数据
            OAuthResponse oauthResponse = OAuthASResponse
                    .tokenResponse(HttpServletResponse.SC_OK)
                    .setAccessToken(accessToken)
                    .setExpiresIn(String.valueOf(TimeUnit.MILLISECONDS.convert(this.expire, TimeUnit.DAYS)))
                    .buildJSONMessage();
            return new ResponseEntity<String>(oauthResponse.getBody(), HttpStatus.valueOf(oauthResponse.getResponseStatus())) ;

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("服务器内部错误，请稍后重试！", HttpStatus.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));    // 响应成功
        }
    }
}
