package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.entity.*;
import com.nuaa.art.vrm.service.dao.*;
import com.nuaa.art.vrm.service.handler.CreateProjectHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("createProjectBaseConceptLibrary")
public class CreateProjectBaseConceptLibrary extends CreateProjectAsNew {

    /**
     * 新建项目
     *
     * @param project 项目
     * @param baseId  基于项目Id
     * @return int
     */
    @Override
    @Transactional
    public int newProject(SystemProject project, Integer baseId) {
        project.setSystemId(null);
        if(daoHandler.getDaoService(SystemProjectService.class).insertSystemProject(project)){
            try {
                copyType(baseId, project.getSystemId());
                copyProperNoun(baseId, project.getSystemId());
                copyConceptItem(baseId, project.getSystemId());
                copyModelClass(baseId, project.getSystemId());
                return 1;
            } catch (Exception e){
                LogUtils.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return -1;
    }


    /**
     * 复制领域概念库
     *
     * @param newsysId 新项目Id
     * @param oldsysId 旧项目Id
     */
    public void copyModelClass(Integer oldsysId, Integer newsysId) {
        // 模式集插入
        List<ModeClass> mc = daoHandler.getDaoService(ModeClassService.class).listModeClassBySystemId(oldsysId);
        Map<Integer,Integer> modeClassIdMap = new HashMap<>();
        if (mc != null) {
            ModeClass modeClass = new ModeClass();
            for (ModeClass m : mc) {
                modeClass.setModeClassId(null);
                modeClass.setModeClassName(m.getModeClassName());
                modeClass.setModeClassDescription(m.getModeClassDescription());
                modeClass.setSystemId(newsysId);
                daoHandler.getDaoService(ModeClassService.class).insertModeClass(modeClass);
                modeClassIdMap.put(m.getModeClassId(),modeClass.getModeClassId());
            }
        }
        mc = null;

        // 模式插入
        List<Mode> mo = daoHandler.getDaoService(ModeService.class).listModeBySystemId(oldsysId);
        if (mo != null) {
            Mode mode = new Mode();
            for (Mode m : mo) {
                mode.setModeId(null);
                mode.setModeName(m.getModeName());
                mode.setInitialStatus(m.getInitialStatus());
                mode.setFinalStatus(m.getFinalStatus());
                mode.setValue(m.getValue());
                mode.setModeClassName(m.getModeClassName());
                mode.setModeClassId(modeClassIdMap.get(m.getModeClassId()));
                mode.setModeDescription(m.getModeDescription());
                mode.setSystemId(newsysId);
                daoHandler.getDaoService(ModeService.class).insertMode(mode);
            }
        }
        mo = null;

        // 模式转换表插入
        List<StateMachine> st = daoHandler.getDaoService(StateMachineService.class).listStateMachineBySystemId(oldsysId);
        if (st != null) {
            StateMachine stateMachine = new StateMachine();
            for (StateMachine sm : st) {
                stateMachine.setStateMachineId(null);
                stateMachine.setSourceState(sm.getSourceState());
                stateMachine.setEndState(sm.getEndState());
                stateMachine.setEvent(sm.getEvent());
                stateMachine.setDependencyModeClass(sm.getDependencyModeClass());
                stateMachine.setDependencyModeClassId(modeClassIdMap.get(sm.getDependencyModeClassId()));
                stateMachine.setSystemId(newsysId);
                daoHandler.getDaoService(StateMachineService.class).insertStateMachine(stateMachine);
            }
        }
        st = null;
        modeClassIdMap = null;
    }

    public void copyConceptItem(Integer oldsysId, Integer newsysId) {
        // 领域概念元素插入
        List<ConceptLibrary> conceptItems = daoHandler.getDaoService(ConceptLibraryService.class).listAllConceptBySystemId(oldsysId);
        if (conceptItems != null) {
            ConceptLibrary variable = new ConceptLibrary();
            for (ConceptLibrary item : conceptItems) {
                variable.setConceptId(null);
                variable.setConceptName(item.getConceptName());
                variable.setConceptDatatype(item.getConceptDatatype());
                variable.setConceptRange(item.getConceptRange());
                variable.setConceptAccuracy(item.getConceptAccuracy());
                variable.setConceptValue(item.getConceptValue());
                variable.setConceptDependencyModeClass(item.getConceptDependencyModeClass());
                variable.setConceptDescription(item.getConceptDescription());
                variable.setConceptType(item.getConceptType());
                variable.setSystemId(newsysId);
                daoHandler.getDaoService(ConceptLibraryService.class).insertConcept(variable);
            }
        }
        conceptItems = null;
    }

    public void copyProperNoun(Integer oldsysId, Integer newsysId) {
        // 专有名词插入
        List<ProperNoun> pn = daoHandler.getDaoService(ProperNounService.class).listProperNounBySystemId(oldsysId);
        if (pn != null) {
            ProperNoun properNoun = new ProperNoun();
            for (ProperNoun pro : pn) {
                properNoun.setProperNounId(null);
                properNoun.setProperNounName(pro.getProperNounName());
                properNoun.setProperNounDescription(pro.getProperNounDescription());
                properNoun.setSystemId(newsysId);
                daoHandler.getDaoService(ProperNounService.class).insertProperNoun(properNoun);
            }
        }
        pn = null;
    }

    public void copyType(Integer oldsysId, Integer newsysId) {
        // 数据类型插入
        List<Type> tp = daoHandler.getDaoService(TypeService.class).listTypeBySystemId(oldsysId);
        if (tp != null) {
            Type type = new Type();
            for (Type tpe : tp) {
                type.setTypeId(null);
                type.setTypeName(tpe.getTypeName());
                type.setDataType(tpe.getDataType());
                type.setTypeRange(tpe.getTypeRange());
                type.setTypeAccuracy(tpe.getTypeAccuracy());
                type.setSystemId(newsysId);
                daoHandler.getDaoService(TypeService.class).insertType(type);
            }
        }
        tp = null;
    }


}
