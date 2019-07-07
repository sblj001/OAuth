package com.yootk.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yootk.service.IRoleAndActionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:spring/spring-base.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRoleAndActionService {
    @Reference
    private IRoleAndActionService roleAndActionService ;
    @Test
    public void testLogin() {
        System.out.println(this.roleAndActionService.get("admin"));
    }
}
