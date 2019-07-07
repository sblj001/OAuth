package com.yootk.oauth.realm;

import com.yootk.oauth.filter.token.OAuthToken;
import com.yootk.oauth.service.IMemberPrivilegeService;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

// 此处不使用扫描配置，通过bean配置文件的模式来完成
public class OAuthRealm extends AuthorizingRealm {
    @Autowired
    private IMemberPrivilegeService memberPrivilegeService ;
    private String clientId ; // 客户端id
    private String clientSecret ; // 客户端密码
    private String redirectUri ; // 返回地址
    private String accessTokenUrl ; // 进行Token交互的地址
    private String memberInfoUrl ; // 获取用户信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.err.println("【1】｛MemberRealm｝============== 用户认证处理 ==============");
        // 1、此时传入的认证的Token实际上就是之前自定义的Token对象
        OAuthToken oauthToken = (OAuthToken) token ;    // 获取OAuth的Token对象实例
        // 2、根据token获取此时的密码（code）
        String code = (String) oauthToken.getCredentials() ; // 获取Code信息
        // 3、用户名现在保存在远程的OAuth之中（Redis里）
        String mid = this.getMemberInfoUrl(code) ; // 根据code获取用户信息
        return new SimpleAuthenticationInfo(mid,code,this.getName());
    }
    private String getMemberInfoUrl(String code) {
        String mid = null ; // 利用oAuth的资源访问获取用户信息
        try {
            // 1、如果要进行OAuth访问，那么就必须配置有一个OAuth客户端处理类
            OAuthClient oauthClient = new OAuthClient(new URLConnectionClient());
            // 2、根据已有的数据的内容创建一个OAuth客户端的请求对象
            OAuthClientRequest oauthClientRequest = OAuthClientRequest
                    .tokenLocation(this.accessTokenUrl) // 设置token的访问地址
                    .setGrantType(GrantType.AUTHORIZATION_CODE) // 设置code的授权类型
                    .setClientId(this.clientId)  // 用户id
                    .setClientSecret(this.clientSecret) // 用户密码
                    .setRedirectURI(this.redirectUri) // 设置返回路径
                    .setCode(code)
                    .buildQueryMessage();
            // 3、如果要想进行token数据的获取，在整个程序上肯定要发送一个POST请求，获取一个Token回应对象
            OAuthJSONAccessTokenResponse tokenResponse = oauthClient.accessToken(oauthClientRequest, OAuth.HttpMethod.POST);
            // 4、通过回应的对象，就可以获取里面所包含的token的数据信息
            String accessToken = tokenResponse.getAccessToken() ; // 获取访问的token处理
            // 5、获取AccessToken的目的在于需要进行mid数据的获取，所以此时继续创建一个新的请求
            // 该请求一定要将获取的accessToken的内容直接传递过去
            OAuthClientRequest memberInfoRequest = new OAuthBearerClientRequest(this.memberInfoUrl).setAccessToken(accessToken).buildQueryMessage();
            // 6、根据客户端的请求将memberInfo的请求进行发送，此时可以使用GET提交模式
            OAuthResourceResponse resourceResponse = oauthClient.resource(memberInfoRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
            // 7、获取了资源访问请求回应对象之后就可以获取对应的mid的数据信息
            mid = resourceResponse.getBody() ;
        } catch (Exception e) {}
        return mid ;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.err.println("【2】｛MemberRealm｝************** 用户授权处理 **************");
        Map<String, Set<String>> map = this.memberPrivilegeService.getByMember((String) principals.getPrimaryPrincipal());
        // 将所有获取的授权信息保存在AuthorizationInfo类的实例之中
        SimpleAuthorizationInfo authz = new SimpleAuthorizationInfo() ; // 返回的授权信息
        authz.setRoles(map.get("allRoles"));
        authz.setStringPermissions(map.get("allActions"));
        return authz;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
    public void setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
    }
    public void setMemberInfoUrl(String memberInfoUrl) {
        this.memberInfoUrl = memberInfoUrl;
    }
}
