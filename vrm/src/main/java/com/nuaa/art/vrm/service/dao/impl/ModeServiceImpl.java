package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.mapper.ModeMapper;
import com.nuaa.art.vrm.service.dao.ModeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_mode】的数据库操作Service实现
* @createDate 2023-06-10 19:04:55
*/
@Service
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
    public void insertMode(Mode mode) {
        save(mode);
    }

    @Override
    public void insertModeList(List<Mode> modeList) {
        saveBatch(modeList);
    }

    @Override
    public void updateMode(Mode mode) {
        updateById(mode);
    }

    @Override
    public void deleteMode(Mode mode) {
        removeById(mode);
    }

    @Override
    public void deleteModeById(Integer systemId) {
        QueryWrapper<Mode> wrapper = new QueryWrapper<>();
        wrapper.eq("systemId",systemId);
        remove(wrapper);
    }

    @Override
    public void deleteModeListByModeClassId(Integer modeClassId) {
        QueryWrapper<Mode> wrapper = new QueryWrapper<>();
        wrapper.eq("modeClassId",modeClassId);
        remove(wrapper);
    }
}




