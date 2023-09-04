package com.nuaa.art.user.service.handler;

import com.nuaa.art.user.entity.User;
import com.nuaa.art.user.model.LoginInfo;
import com.nuaa.art.user.model.UserInfo;

public interface UserHandler {
    LoginInfo login(User user);

    UserInfo getUserInfo(String userName);

}
