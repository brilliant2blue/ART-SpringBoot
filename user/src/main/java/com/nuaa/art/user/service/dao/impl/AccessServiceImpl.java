package com.nuaa.art.user.service.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.user.entity.Access;
import com.nuaa.art.user.mapper.AccessMapper;
import com.nuaa.art.user.service.dao.AccessService;
import org.springframework.stereotype.Service;

/**
* @author konsin
* @description 针对表【access】的数据库操作Service实现
* @createDate 2023-06-10 19:05:15
*/
@Service
public class AccessServiceImpl extends ServiceImpl<AccessMapper, Access>
    implements AccessService{

    @Override
    public Access getAccess(Integer id){
        return getBaseMapper().selectById(id);
    };
}




