package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.Type;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_type】的数据库操作Service
* @createDate 2023-06-10 19:01:41
*/
public interface TypeService extends IService<Type> {
    public List<Type> listType();
    public Type getTypeById(Integer id);
    public List<Type> listTypeBySystemId(Integer systemId);
    public Type getTypeByNameandId(@Param("typeName")String name,@Param("systemId")Integer systemId);
    public boolean insertType(Type type);
    public boolean updateType(Type type);
    public boolean deleteType(Type type);
    public boolean deleteTypeById(Integer systemId);
    public boolean deleteTypeByProId(Integer proId);
}
