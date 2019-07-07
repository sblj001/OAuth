package com.yootk.oauth.service;

import com.yootk.vo.Client;
import com.yootk.vo.Member;

public interface IOAuthService {
    public Client get(String mid) ;

    /**
     * 进行用户认证信息的获取
     * @param mid 要获取的用户id
     * @return 用户的完整信息
     */
    public Member getMember(String mid) ;
}
