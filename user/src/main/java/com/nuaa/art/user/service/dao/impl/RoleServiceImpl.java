package com.nuaa.art.user.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.user.entity.Role;
import com.nuaa.art.user.service.dao.RoleService;
import com.nuaa.art.user.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author konsin
* @description 针对表【sys_role】的数据库操作Service实现
* @createDate 2023-07-19 17:34:05
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

    @Override
    public Role getRoleByRoleId(Integer id) {
        return getOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleId, id));
    }
}




