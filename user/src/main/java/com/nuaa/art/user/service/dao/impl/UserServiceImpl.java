package com.nuaa.art.user.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.user.entity.User;
import com.nuaa.art.user.mapper.UserMapper;
import com.nuaa.art.user.service.dao.UserService;
import org.springframework.stereotype.Service;

/**
* @author konsin
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-06-10 19:05:07
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public User selectUserByName(String name){
        QueryWrapper wrapper = new QueryWrapper<User>();
        wrapper.eq("username",name);
        return getBaseMapper().selectOne(wrapper);
    };
}




