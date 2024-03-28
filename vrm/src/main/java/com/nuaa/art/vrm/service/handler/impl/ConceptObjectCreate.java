package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("concept-object")
public class ConceptObjectCreate implements ModelCreateHandler {

    @Resource
    DaoHandler daoHandler;

    @Override
    public Object createModel(Integer systemId) {

        // 设定系统信息
        VRM vrm = new VRM();
        vrm.creatDate();

        vrm.setSystem(daoHandler.getDaoService(SystemProjectService.class).getSystemProjectById(systemId));

        // 设定类型信息
        List<Type> types = daoHandler.getDaoService(TypeService.class).listTypeBySystemId(systemId);
        vrm.setTypes((ArrayList<Type>) types);

        //设定常量信息
        List<ConceptLibrary> constants =
                daoHandler.getDaoService(ConceptLibraryService.class).listConstVariableBySystemId(systemId);
        vrm.setConstants((ArrayList<ConceptLibrary>) constants);

        // 设定输入信息
        List<ConceptLibrary> inputs =
                daoHandler.getDaoService(ConceptLibraryService.class).listInputVariableBySystemId(systemId);
        vrm.setInputs((ArrayList<ConceptLibrary>) inputs);

        // 设定中间变量信息
        List<ConceptLibrary> terms =
                daoHandler.getDaoService(ConceptLibraryService.class).listTermVariableBySystemId(systemId);
        vrm.setTerms((ArrayList<ConceptLibrary>) terms);

        //设定输出变量信息
        List<ConceptLibrary> outputs =
                daoHandler.getDaoService(ConceptLibraryService.class).listOutputVariableBySystemId(systemId);
        vrm.setOutputs((ArrayList<ConceptLibrary>) outputs);

        //将领域概念模式集、模式和模式转换信息 转换为 模式转换表
        List<ModeClass> modeClasses = daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(systemId);
        List<Mode> modes = daoHandler.getDaoService(ModeService.class).listModeBySystemId(systemId);
        List<StateMachine> modeTrans = daoHandler.getDaoService(StateMachineService.class).listStateMachineBySystemId(systemId);

        for (ModeClass mc : modeClasses) {
            ModeClassOfVRM MC = new ModeClassOfVRM();
            MC.setModeClass(mc);

            //添加节点stateList
            for (Mode m : modes) {
                if (m.getModeClassName().equals(mc.getModeClassName())) {
                    MC.getModes().add(m);
                }
            }

            //添加节点stateTransition
            for (StateMachine stateM : modeTrans) {
                if (stateM.getDependencyModeClass().equals(mc.getModeClassName())) {
                    MC.getModeTrans().add(stateM);
                }
            }
            vrm.getModeClass().add(MC);
        }

        return vrm;
    }

    @Override
    public Object modelFile(Integer systemId, String fileName) {
        return null;
    }
}
