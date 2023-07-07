package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.ModeClass;
import com.nuaa.art.vrm.mapper.ModeClassMapper;
import com.nuaa.art.vrm.service.dao.ModeClassService;
import com.nuaa.art.vrm.service.dao.ModeService;
import com.nuaa.art.vrm.service.dao.StateMachineService;
import jakarta.annotation.Resource;
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
    @Resource
    ModeService modeService;
    @Resource
    StateMachineService stateMachineService;

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
        return list(new QueryWrapper<ModeClass>().eq("systemId", systemId));
    }

    @Override
    public ModeClass getModeClassByNameandId(@Param("modeClassName") String name, @Param("systemId") Integer systemId) {
        return getOne(new QueryWrapper<ModeClass>()
                .eq("modeClassName", name)
                .eq("systemId", systemId));
    }

    @Override
    public boolean insertModeClass(ModeClass modeClass) {
        return save(modeClass);
    }

    @Override
    public boolean updateModeClass(ModeClass modeClass) {
        ModeClass mc = getById(modeClass.getModeClassId());
        List<Mode> modes = modeService.listModeByNameandId(mc.getModeClassName(), mc.getSystemId());
        for (Mode mode : modes) {
            mode.setModeClassName(modeClass.getModeClassName());
            if (!modeService.updateMode(mode)) {
                return false;
            }
        }

        return updateById(modeClass);

    }

    @Override
    public boolean deleteModeClass(ModeClass modeClass) {

        if (modeService.deleteModeListByModeClassId(modeClass.getModeClassId())) {
            return removeById(modeClass);
        } else {
            return false;
        }

    }

    @Override
    public boolean deleteModeClassById(Integer systemId) {

        if (modeService.deleteModeById(systemId)) {
            return remove(new QueryWrapper<ModeClass>().eq("systemId", systemId));
        } else {
            return false;
        }

    }

    @Override
    public boolean deleteModeClassByProId(Integer proId) {
        return remove(new QueryWrapper<ModeClass>().eq("systemId", proId));
    }
}




