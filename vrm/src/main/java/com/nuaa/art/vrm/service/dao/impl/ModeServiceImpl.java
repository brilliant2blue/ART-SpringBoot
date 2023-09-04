package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.mapper.ModeMapper;
import com.nuaa.art.vrm.service.dao.ModeService;
import com.nuaa.art.vrm.service.dao.StateMachineService;
import jakarta.annotation.Resource;
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

    @Resource
    StateMachineService stateMachineService;

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
    public List<Mode> listModeByClassId(Integer id) {
        return list(new LambdaQueryWrapper<Mode>().eq(Mode::getModeClassId, id));
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
    @Transactional
    public boolean updateMode(Mode mode) {
        Mode m = getModeById(mode.getModeId());
        List<StateMachine> stateMachines = stateMachineService.listStateMachineByDenpdencyId(m.getModeClassId());
        for(StateMachine stateMachine : stateMachines){
            if(stateMachine.getSourceState().equals(m.getModeName())){
                stateMachine.setSourceState(mode.getModeName());
            } else if(stateMachine.getEndState().equals(m.getModeName())){
                stateMachine.setEndState(mode.getModeName());
            }
            stateMachineService.updateStateMachine(stateMachine);
        }
        return updateById(mode);
    }

    @Override
    @Transactional
    public boolean deleteMode(Mode mode) {
        List<StateMachine> stateMachines = stateMachineService.listStateMachineByDenpdencyId(mode.getModeClassId());
        for(StateMachine stateMachine : stateMachines){
            if(stateMachine.getSourceState().equals(mode.getModeName())){
                stateMachineService.deleteStateMachine(stateMachine);
            } else if(stateMachine.getEndState().equals(mode.getModeName())){
                stateMachineService.deleteStateMachine(stateMachine);
            }
        }
        return removeById(mode);
    }

    @Override
    @Transactional
    public boolean deleteModeById(Integer systemId) {
        List<Mode> modes = listModeBySystemId(systemId);
        for(Mode mode:modes){
            if(!deleteMode(mode)){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean deleteModeListByModeClassId(Integer modeClassId) {
        QueryWrapper<Mode> wrapper = new QueryWrapper<>();
        wrapper.eq("modeClassId",modeClassId);
        List<Mode> modes = list(wrapper);
        for(Mode mode : modes){
            if(!deleteMode(mode)){
                return false;
            }
        }
        return true;
    }
}




