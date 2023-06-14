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
    public void insertType(Type type) {
        save(type);
    }

    @Override
    public void updateType(Type type) {
        updateById(type);
    }

    @Override
    public void deleteType(Type type) {
        removeById(type);
    }

    @Override
    public void deleteTypeById(Integer systemId) {
        remove(new QueryWrapper<Type>().eq("systemId",systemId));
    }

    @Override
    public void deleteTypeByProId(Integer proId) {
        removeById(proId);
    }
}




