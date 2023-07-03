package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.mapper.TypeMapper;
import com.nuaa.art.vrm.service.dao.TypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_type】的数据库操作Service实现
* @createDate 2023-06-10 19:01:41
*/
@Service
@Transactional
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type>
    implements TypeService{
    @Override
    public List<Type> listType() {
        return list();
    }

    @Override
    public Type getTypeById(Integer id) {
        return getById(id);
    }

    @Override
    public List<Type> listTypeBySystemId(Integer systemId) {
        return list(new QueryWrapper<Type>().eq("systemId",systemId));
    }

    @Override
    public Type getTypeByNameandId(String name, Integer systemId) {
        return getOne(new QueryWrapper<Type>()
                .eq("typeName",name)
                .eq("systemId",systemId));
    }

    @Override
    public boolean insertType(Type type) {
        return save(type);
    }

    @Override
    public boolean updateType(Type type) {
        return updateById(type);
    }

    @Override
    public boolean deleteType(Type type) {
        return removeById(type);
    }

    @Override
    public boolean deleteTypeById(Integer systemId) {
        return remove(new QueryWrapper<Type>().eq("systemId",systemId));
    }

    @Override
    public boolean deleteTypeByProId(Integer proId) {
        return removeById(proId);
    }
}




