package com.yootk.client.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yootk.client.dao.IActionDAO;
import com.yootk.client.dao.IRoleDAO;
import com.yootk.service.IRoleAndActionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
@Service
public class RoleAndActionServiceImpl implements IRoleAndActionService {
    @Autowired
    private IRoleDAO roleDAO ;
    @Autowired
    private IActionDAO actionDAO ;
    @Override
    public Map<String, Object> get(String mid) {
        Map<String,Object> result = new HashMap<>() ;
        result.put("allRoles",this.roleDAO.findAllByMember(mid)) ;
        result.put("allActions",this.actionDAO.findAllByMember(mid)) ;
        return result;
    }
}
