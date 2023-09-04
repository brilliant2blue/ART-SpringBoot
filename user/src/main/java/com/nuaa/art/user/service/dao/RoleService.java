package com.nuaa.art.user.service.dao;

import com.nuaa.art.user.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author konsin
* @description 针对表【sys_role】的数据库操作Service
* @createDate 2023-07-19 17:34:05
*/
public interface RoleService extends IService<Role> {
    Role getRoleByRoleId(Integer id);
}
