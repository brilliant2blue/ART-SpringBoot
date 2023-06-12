package com.nuaa.art.user.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.user.entity.RoleAccess;

import java.util.List;

/**
* @author konsin
* @description 针对表【role_access】的数据库操作Service
* @createDate 2023-06-10 19:05:11
*/
public interface RoleAccessService extends IService<RoleAccess> {
    public List<RoleAccess> listRoleAccess(Integer roleId);
    public void addRoleAccess(RoleAccess roleAccess);
}
