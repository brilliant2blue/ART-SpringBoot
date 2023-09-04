package com.nuaa.art.user.mapper;

import com.nuaa.art.user.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author konsin
* @description 针对表【sys_role】的数据库操作Mapper
* @createDate 2023-07-19 17:34:05
* @Entity com.nuaa.art.user.entity.Role
*/
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}




