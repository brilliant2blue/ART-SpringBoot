package com.nuaa.art.main.controller;

import com.nuaa.art.user.entity.User;
import com.nuaa.art.user.service.handler.UserHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    UserHandlerService userHandler;

    @PostMapping("/login")
    public Object login(@RequestBody User user){
        return userHandler.login(user);
    }

}
