package com.yootk.oauth.service;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

public interface IDeptService {
    @RequiresRoles("member") // 拥有member角色才可以访问
    @RequiresPermissions("dept:add") // 拥有指定的权限才可以访问
    // @RequiresAuthentication // 必须认证之后才可以进行此路径的访问
    public void list() ;
}
