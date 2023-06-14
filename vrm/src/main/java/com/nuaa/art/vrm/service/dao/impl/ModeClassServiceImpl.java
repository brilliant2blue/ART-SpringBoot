package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.ModeClass;
import com.nuaa.art.vrm.mapper.ModeClassMapper;
import com.nuaa.art.vrm.service.dao.ModeClassService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_modeclass】的数据库操作Service实现
* @createDate 2023-06-10 19:04:50
*/
@Service
@Transactional
public class ModeClassServiceImpl extends ServiceImpl<ModeClassMapper, ModeClass>
    implements ModeClassService {
    @Override
    public List<ModeClass> listModeClass() {
        return list();
    }

    @Override
    public ModeClass getModeClassById(Integer id) {
        return getById(id);
    }

    @Override
    public List<ModeClass> listModeClassBySystemId(Integer systemId) {
        return list(new QueryWrapper<ModeClass>().eq("systemId",systemId));
    }

    @Override
    public ModeClass getModeClassByNameandId(@Param("modeClassName") String name, @Param("systemId") Integer systemId) {
        return getOne(new QueryWrapper<ModeClass>()
                .eq("modeClassName",name)
                .eq("systemId",systemId));
    }

    @Override
    public void insertModeClass(ModeClass modeClass) {
        save(modeClass);
    }

    @Override
    public void updateModeClass(ModeClass modeClass) {
        updateById(modeClass);
    }

    @Override
    public void deleteModeClass(ModeClass modeClass) {
        removeById(modeClass);
    }

    @Override
    public void deleteModeClassById(Integer systemId) {
        remove(new QueryWrapper<ModeClass>().eq("systemId",systemId));
    }

    @Override
    public void deleteModeClassByProId(Integer proId) {
        removeById(proId);
    }
}




