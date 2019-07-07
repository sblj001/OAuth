package com.yootk.client.dao;

import java.util.Set;

public interface IRoleDAO {
    // 根据用户编号查询所有的角色id
    public Set<String> findAllByMember(String mid) ;
}
