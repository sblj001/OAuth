package com.yootk.client.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yootk.client.dao.IClientDAO;
import com.yootk.service.IClientService;
import com.yootk.vo.Client;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ClientServiceImpl implements IClientService {
    @Autowired
    private IClientDAO clientDAO ;

    @Override
    public Client get(String clientId) {
        return this.clientDAO.findById(clientId);
    }
}
