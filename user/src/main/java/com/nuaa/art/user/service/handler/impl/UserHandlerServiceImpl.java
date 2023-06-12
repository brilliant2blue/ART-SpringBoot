package com.nuaa.art.user.service.handler.impl;

import com.nuaa.art.user.common.RoleConstant;
import com.nuaa.art.user.entity.RoleAccess;
import com.nuaa.art.user.entity.User;
import com.nuaa.art.user.model.UserResult;
import com.nuaa.art.user.service.handler.UserHandlerService;
import com.nuaa.art.user.service.dao.AccessService;
import com.nuaa.art.user.service.dao.RoleAccessService;
import com.nuaa.art.user.service.dao.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserHandlerServiceImpl implements UserHandlerService {
    @Autowired
    UserService userService;

    @Autowired
    RoleAccessService roleAccessService;

    @Autowired
    AccessService accessService;



    @Override
    public Object login(User user) {
        UserResult result = new UserResult();
        User thisuser = userService.selectUserByName(user.getUsername());
        if(thisuser.getPassword().equals(user.getPassword())){
            result.result = true;
            result.username = user.getUsername();
            result.rolename = RoleConstant.getRoleNameById(thisuser.getRole());
            result.role = thisuser.getRole();
            result.accesses = roleAccessService.listRoleAccess(thisuser.getRole());
            result.accessStrings  = new ArrayList<>();
            for(RoleAccess roleAccess: result.accesses) {
                result.accessStrings.add(accessService.getAccess(roleAccess.getAccessId()).getAccess());
            }
        } else {
            result.result = false;
        }
        return result;
    }

    @Override
    public Object logout(User user) {
        return null;
    }
}
