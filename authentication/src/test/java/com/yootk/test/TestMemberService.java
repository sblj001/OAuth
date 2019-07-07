package com.yootk.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yootk.service.IMemberService;
import com.yootk.util.encrypt.EncryptUtil;
import com.yootk.vo.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:META-INF/spring/spring-base.xml",
        "classpath:META-INF/spring/spring-mybatis.xml",
        "classpath:META-INF/spring/spring-transaction.xml",
        "classpath:META-INF/spring/spring-cache.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestMemberService {
    @Reference
    private IMemberService memberInstance ;
    @Test
    public void testLogin() {
        Member vo = new Member();
        vo.setMid("admin");
        vo.setPassword(EncryptUtil.encode("hello"));
        System.out.println(this.memberInstance.login(vo));
    }
}
