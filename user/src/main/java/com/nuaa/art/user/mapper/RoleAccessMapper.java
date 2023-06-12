package com.nuaa.art.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuaa.art.user.entity.RoleAccess;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
* @author konsin
* @description 针对表【role_access】的数据库操作Mapper
* @createDate 2023-06-10 19:05:11
* @Entity com.nuaa.art.entity.RoleAccess
*/
@Mapper
public interface RoleAccessMapper extends BaseMapper<RoleAccess> {
    @Insert("<script>"
           + "insert into role_access"
           + "<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">"
           + "<if test=\"roleId != null\">"
           + "role_id,"
           + "</if>"
           + "<if test=\"accessId != null\">"
           + "access_id,"
           + "</if>"
           + "</trim>"
           + "<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">"
           + "<if test=\"roleId != null\">"
           + "#{roleId,jdbcType=INTEGER},"
           + "</if>"
           + "<if test=\"accessId != null\">"
           + "#{accessId,jdbcType=INTEGER},"
           + "</if>"
           + "</trim>"
           + "</script>")
    void addRoleAccess(RoleAccess roleAccess);

}




