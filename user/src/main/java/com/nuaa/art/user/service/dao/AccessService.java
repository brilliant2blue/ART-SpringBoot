package com.nuaa.art.user.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.user.entity.Access;

/**
* @author konsin
* @description 针对表【access】的数据库操作Service
* @createDate 2023-06-10 19:05:15
*/
public interface AccessService extends IService<Access> {
    public Access getAccess(Integer id);
}
