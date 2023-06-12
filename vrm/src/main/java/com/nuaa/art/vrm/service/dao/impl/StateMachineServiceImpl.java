package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.mapper.StateMachineMapper;
import com.nuaa.art.vrm.service.dao.StateMachineService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_statemachine】的数据库操作Service实现
* @createDate 2023-06-10 19:04:11
*/
@Service
public class StateMachineServiceImpl extends ServiceImpl<StateMachineMapper, StateMachine>
    implements StateMachineService {

    @Override
    public List<StateMachine> listStateMachine() {
        return list();
    }

    @Override
    public StateMachine getStateMachineById(Integer id) {
        return getById(id);
    }

    @Override
    public List<StateMachine> listStateMachineBySystemId(Integer systemId) {
        return list(new QueryWrapper<StateMachine>().eq("systemId",systemId));
    }

    @Override
    public List<StateMachine> listStateMachineByDenpdencyandId(String denpedencyModeClass, Integer systemId) {
        return list(new QueryWrapper<StateMachine>()
                .eq("dependencyModeClass",denpedencyModeClass)
                .eq("systemId",systemId));
    }

    @Override
    public void insertStateMachine(StateMachine stateMachine) {
        save(stateMachine);
    }

    @Override
    public void insertStateMachineList(List<StateMachine> stateMachineList) {
        saveBatch(stateMachineList);
    }

    @Override
    public void updateStateMachine(StateMachine stateMachine) {
        updateById(stateMachine);
    }

    @Override
    public void deleteStateMachine(StateMachine stateMachine) {
        removeById(stateMachine);
    }

    @Override
    public void deleteStateMachineById(Integer systemId) {
        remove(new QueryWrapper<StateMachine>().eq("systemId",systemId));
    }

    @Override
    public void deleteStateMachineListByModeClassId(Integer modeClassId) {
        remove(new QueryWrapper<StateMachine>().eq("dependencyModeClassId",modeClassId));
    }
}




