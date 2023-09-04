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
    List<StateMachine> listStateMachine();
    StateMachine getStateMachineById(Integer id);
    List<StateMachine> listStateMachineBySystemId(Integer systemId);
    List<StateMachine> listStateMachineByDenpdencyId(Integer denpdencyId);
    boolean insertStateMachine(StateMachine stateMachine);
    boolean insertStateMachineList(@Param("stateMachineList")List<StateMachine> stateMachineList);
    boolean updateStateMachine(StateMachine stateMachine);
    boolean deleteStateMachine(StateMachine stateMachine);
    boolean deleteStateMachineById(Integer systemId);
    boolean deleteStateMachineListByModeClassId(Integer modeClassId);
}
