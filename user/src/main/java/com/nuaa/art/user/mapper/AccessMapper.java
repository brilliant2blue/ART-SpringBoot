package com.nuaa.art.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuaa.art.user.entity.Access;
import org.apache.ibatis.annotations.Mapper;

/**
* @author konsin
* @description 针对表【access】的数据库操作Mapper
* @createDate 2023-06-10 19:05:15
* @Entity com.nuaa.art.entity.Access
*/
@Mapper
public interface AccessMapper extends BaseMapper<Access> {
}




