package com.nuaa.art.user.service.handler.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nuaa.art.user.common.utils.JwtUtils;
import com.nuaa.art.user.entity.Role;
import com.nuaa.art.user.entity.RoleMenu;
import com.nuaa.art.user.entity.User;
import com.nuaa.art.user.model.UserInfo;
import com.nuaa.art.user.model.LoginInfo;
import com.nuaa.art.user.service.dao.*;
import com.nuaa.art.user.service.handler.UserHandler;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserHandlerImpl implements UserHandler {
    @Autowired
    UserService userService;

    @Autowired
    RoleMenuService roleMenuService;

    @Autowired
    RoleService roleService;



    @Override
    public LoginInfo login(User user) {
        User thisuser = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword()));
        if(thisuser != null){
            LoginInfo result = new LoginInfo();
            result.token = JwtUtils.generateToken(thisuser);
            result.username = thisuser.getUsername();
            result.realname = thisuser.getRealname();
            result.email = thisuser.getEmail();
            Role role = roleService.getRoleByRoleId(thisuser.getRole());
            if(role != null) {
                result.rolename = role.getRoleName();
                result.role = thisuser.getRole();
            }
            List<String> menu=  new ArrayList<>();
            for(RoleMenu ra: roleMenuService.listRoleMenu(result.role)){
                menu.add(ra.getMenu());
            }
            result.menus = menu;
            return result;
        }
        return null;
    }

    @Override
    public UserInfo getUserInfo(String userName) {
        User thisuser = userService.selectUserByName(userName);
        if(thisuser != null){
            UserInfo result = new UserInfo();
            result.username = userName;
            result.realname = thisuser.getRealname();
            result.email = thisuser.getEmail();
            Role role = roleService.getRoleByRoleId(thisuser.getRole());
            if(role != null) {
                result.rolename = role.getRoleName();
                result.role = thisuser.getRole();
            }
            return result;
        }
        return null;
    }
}
