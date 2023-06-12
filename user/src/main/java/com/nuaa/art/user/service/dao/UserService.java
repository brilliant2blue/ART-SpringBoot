package com.nuaa.art.user.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.user.entity.User;

/**
* @author konsin
* @description 针对表【user】的数据库操作Service
* @createDate 2023-06-10 19:05:07
*/
public interface UserService extends IService<User> {

    public User selectUserByName(String name);
}
