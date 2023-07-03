package com.nuaa.art.vrm.service.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.mapper.StateMachineMapper;
import com.nuaa.art.vrm.service.dao.StateMachineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_statemachine】的数据库操作Service实现
* @createDate 2023-06-10 19:04:11
*/
@Service
@Transactional
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
    public boolean insertStateMachine(StateMachine stateMachine) {
        return save(stateMachine);
    }

    @Override
    public boolean insertStateMachineList(List<StateMachine> stateMachineList) {
        return saveBatch(stateMachineList);
    }

    @Override
    public boolean updateStateMachine(StateMachine stateMachine) {
        return updateById(stateMachine);
    }

    @Override
    public boolean deleteStateMachine(StateMachine stateMachine) {
        return removeById(stateMachine);
    }

    @Override
    public boolean deleteStateMachineById(Integer systemId) {
        return remove(new QueryWrapper<StateMachine>().eq("systemId",systemId));
    }

    @Override
    public boolean deleteStateMachineListByModeClassId(Integer modeClassId) {
        return remove(new QueryWrapper<StateMachine>().eq("dependencyModeClassId",modeClassId));
    }
}




