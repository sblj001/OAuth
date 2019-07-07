package com.yootk.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yootk.service.IMemberService;
import com.yootk.util.encrypt.EncryptUtil;
import com.yootk.vo.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:spring/spring-base.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestMemberService {
    @Reference
    private IMemberService memberService ;
    @Test
    public void testGet() {
        System.out.println(this.memberService.get("admin"));
    }
}
