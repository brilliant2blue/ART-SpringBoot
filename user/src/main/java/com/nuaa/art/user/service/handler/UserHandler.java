package com.nuaa.art.user.service.handler;

import com.nuaa.art.user.entity.User;

public interface UserHandler {
    Object login(User user);
    Object logout(User user);

}
