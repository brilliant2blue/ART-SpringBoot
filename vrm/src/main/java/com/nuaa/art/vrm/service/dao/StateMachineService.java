package com.nuaa.art.vrm.service.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nuaa.art.vrm.entity.StateMachine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author konsin
* @description 针对表【vrm_statemachine】的数据库操作Service
* @createDate 2023-06-10 19:04:11
*/
public interface StateMachineService extends IService<StateMachine> {
    public List<StateMachine> listStateMachine();
    public StateMachine getStateMachineById(Integer id);
    public List<StateMachine> listStateMachineBySystemId(Integer systemId);
    public List<StateMachine> listStateMachineByDenpdencyandId(@Param("dependencyModeClass")String denpedencyModeClass, @Param("systemId")Integer systemId);
    public void insertStateMachine(StateMachine stateMachine);
    public void insertStateMachineList(@Param("stateMachineList")List<StateMachine> stateMachineList);
    public void updateStateMachine(StateMachine stateMachine);
    public void deleteStateMachine(StateMachine stateMachine);
    public void deleteStateMachineById(Integer systemId);
    public void deleteStateMachineListByModeClassId(Integer modeClassId);
}
