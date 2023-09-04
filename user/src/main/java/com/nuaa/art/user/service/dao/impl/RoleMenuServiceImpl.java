package com.nuaa.art.user.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.user.entity.RoleMenu;
import com.nuaa.art.user.service.dao.RoleMenuService;
import com.nuaa.art.user.mapper.RoleMenuMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author konsin
* @description 针对表【sys_role_menu】的数据库操作Service实现
* @createDate 2023-07-20 16:59:23
*/
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu>
    implements RoleMenuService{

    @Override
    public List<RoleMenu> listRoleMenu(Integer roleId){
        LambdaQueryWrapper queryWrapper = new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId,roleId);
        return getBaseMapper().selectList(queryWrapper);
    };

}




