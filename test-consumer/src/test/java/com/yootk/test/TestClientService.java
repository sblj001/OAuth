package com.yootk.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yootk.service.IClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:spring/spring-base.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestClientService {
    @Reference
    private IClientService clientService ;
    @Test
    public void testGet() {
        System.out.println(this.clientService.get("client000111"));
    }
}
