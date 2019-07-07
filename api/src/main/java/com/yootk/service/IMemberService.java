package com.yootk.service;

import com.yootk.vo.Member;

public interface IMemberService {
    /**
     * 实现登录的认证处理，如果用户名和密码正确则将数据信息正确返回
     * 包含有用户的基本内容（姓名、性别）
     * @param info 包含有登录信息的VO类
     * @return 登录成功的VO类，如果登录失败，返回null
     */
    public Member login(Member info) ;
    public Member get(String mid) ;
}
