package com.nuaa.art.user.controller;

import com.nuaa.art.user.entity.User;
import com.nuaa.art.user.service.handler.UserHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * @author konsin
 * @date 2023/06/12
 */
@RestController
@CrossOrigin
public class UserController {
    @Autowired
    UserHandler userHandler;

    /**
     * 登录
     *
     * @param user 用户
     * @return {@link Object}
     */
    @PostMapping("/login")
    public Object login(@RequestBody User user){
        return userHandler.login(user);
    }

}
