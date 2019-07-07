package com.yootk.client.dao;

import java.util.Set;

public interface IActionDAO {
    // 根据用户编号获取对应的所有权限
    public Set<String> findAllByMember(String mid) ;
}
