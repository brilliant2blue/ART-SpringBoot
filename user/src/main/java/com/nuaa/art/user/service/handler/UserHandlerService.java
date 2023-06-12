package com.nuaa.art.user.service.handler;

import com.nuaa.art.user.entity.User;

public interface UserHandlerService {
    Object login(User user);
    Object logout(User user);

}
