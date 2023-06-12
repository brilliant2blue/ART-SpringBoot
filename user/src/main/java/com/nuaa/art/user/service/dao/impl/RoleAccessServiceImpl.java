package com.nuaa.art.user.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.user.entity.RoleAccess;
import com.nuaa.art.user.mapper.RoleAccessMapper;
import com.nuaa.art.user.service.dao.RoleAccessService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author konsin
* @description 针对表【role_access】的数据库操作Service实现
* @createDate 2023-06-10 19:05:11
*/
@Service
public class RoleAccessServiceImpl extends ServiceImpl<RoleAccessMapper, RoleAccess>
    implements RoleAccessService{

    @Override
    public List<RoleAccess> listRoleAccess(Integer roleId){
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("roleId",roleId);
        return getBaseMapper().selectList(queryWrapper);
    };

    @Override
    public void addRoleAccess(RoleAccess roleAccess){
        getBaseMapper().addRoleAccess(roleAccess);
    };

}




