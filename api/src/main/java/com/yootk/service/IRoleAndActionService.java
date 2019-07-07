package com.yootk.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.Map;
@CacheConfig(cacheNames = "authz")
public interface IRoleAndActionService {
    /**
     * 根据指定的用户获取用户的角色与权限
     * @param mid 用户的编号
     * @return 返回的内容包含两块数据：
     * 1、key = allRoles、value = 用户对应的所有角色id；
     * 2、key = allActions、value = 用户对应的所有权限id
     */
    @Cacheable
    public Map<String,Object> get(String mid) ;
}
