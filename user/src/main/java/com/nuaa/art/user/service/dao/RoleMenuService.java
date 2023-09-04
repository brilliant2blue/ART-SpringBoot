package com.nuaa.art.user.service.dao;

import com.nuaa.art.user.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author konsin
* @description 针对表【sys_role_menu】的数据库操作Service
* @createDate 2023-07-20 16:59:23
*/
public interface RoleMenuService extends IService<RoleMenu> {
    List<RoleMenu> listRoleMenu(Integer roleId);
}
