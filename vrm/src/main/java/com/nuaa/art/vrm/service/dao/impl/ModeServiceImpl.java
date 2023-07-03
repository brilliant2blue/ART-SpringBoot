package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.mapper.ModeMapper;
import com.nuaa.art.vrm.service.dao.ModeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_mode】的数据库操作Service实现
* @createDate 2023-06-10 19:04:55
*/
@Service
@Transactional
public class ModeServiceImpl extends ServiceImpl<ModeMapper, Mode>
    implements ModeService{

    @Override
    public List<Mode> listMode() {
        return list();
    }

    @Override
    public Mode getModeById(Integer id) {
        return getById(id);
    }

    @Override
    public Mode getModeByNameAndId(String name, Integer id) {
        return null;
    }

    @Override
    public List<Mode> listModeBySystemId(Integer systemId) {
        QueryWrapper<Mode> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId",systemId);
        return list(wrapper);
    }

    @Override
    public List<Mode> listModeByNameandId(String modelClassName, Integer systemId) {
        QueryWrapper<Mode> wrapper = new QueryWrapper<>();
        wrapper.eq("modeClassName", modelClassName).eq("systemId",systemId);
        return list(wrapper);
    }

    @Override
    public boolean insertMode(Mode mode) {
        return save(mode);
    }

    @Override
    public boolean insertModeList(List<Mode> modeList) {
        return saveOrUpdateBatch(modeList);
    }

    @Override
    public boolean updateMode(Mode mode) {
        return updateById(mode);
    }

    @Override
    public boolean deleteMode(Mode mode) {
        return removeById(mode);
    }

    @Override
    public boolean deleteModeById(Integer systemId) {
        QueryWrapper<Mode> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId",systemId);
        return remove(wrapper);
    }

    @Override
    public boolean deleteModeListByModeClassId(Integer modeClassId) {
        QueryWrapper<Mode> wrapper = new QueryWrapper<>();
        wrapper.eq("modeClassId",modeClassId);
        return remove(wrapper);
    }
}




