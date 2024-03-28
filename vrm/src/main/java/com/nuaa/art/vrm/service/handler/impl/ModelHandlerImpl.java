package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.model.VariableRelationModel;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.hvrm.ModuleTree;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrm.service.handler.ModelHandler;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelHandlerImpl implements ModelHandler {
    /**
     * 从具有层次化的模型中生成数个不具备层次化特征的子模型。
     *
     * @param hvrm Hvrm
     * @return {@link List}<{@link T2}>
     */
    @Override
    public <T1 extends HVRM, T2 extends VRM> List<T2> getModuleModels(T1 hvrm, List<T2> models ,Class<T2> tClass) {
        readModules(null, hvrm, models, tClass);
        for(ModuleTree mt: hvrm.getModules()){
            readModules(mt, hvrm, models, tClass);
        }
        return models;
    }

    private  <T1 extends HVRM, T2 extends VRM> void readModules(ModuleTree moduleTree, T1 hvrm , List<T2> models, Class<T2> tClass){
        T2 module = null;
        if(moduleTree == null || moduleTree.getId().equals(0)){
            module = newModuleModel(null, hvrm, tClass);
            module.getSystem().setSystemName("默认模块");
        } else {
            if(!moduleTree.getChildren().isEmpty() ){
                for(ModuleTree mt: moduleTree.getChildren()){
                    readModules(mt, hvrm, models, tClass);
                }
            }
            module = newModuleModel(moduleTree, hvrm, tClass);
            module.getSystem().setSystemName(moduleTree.getName());
        }

        models.add(module);

    }

    @SneakyThrows
    private <T1 extends HVRM, T2 extends VRM> T2 newModuleModel(ModuleTree moduleTree, T1 hvrm, Class<T2> tClass){
        Integer moduleId;
        if(moduleTree != null){
            moduleId = moduleTree.getId();
        } else {
            moduleId = 0;
        }

        //T2 model = tClass.getDeclaredConstructor().newInstance();
        T2 model = tClass.getDeclaredConstructor().newInstance();

        model.setTypes(hvrm.getTypes());
        model.setConstants(hvrm.getConstants());
        model.setInputs((ArrayList<ConceptLibrary>) hvrm.getInputs().stream()
                .filter((iv)->iv.getInPort().contains(moduleId) || iv.getOutPort().contains(moduleId))
                .map((iv)-> (ConceptLibrary)iv).collect(Collectors.toList()));

        model.setTerms((ArrayList<ConceptLibrary>) hvrm.getTerms().stream()
                .filter((tv)->tv.getInPort().contains(moduleId) || tv.getOutPort().contains(moduleId))
                .map((tv)-> (ConceptLibrary)tv).collect(Collectors.toList()));

        model.setOutputs((ArrayList<ConceptLibrary>) hvrm.getOutputs().stream()
                .filter((ov)->ov.getInPort().contains(moduleId) || ov.getOutPort().contains(moduleId))
                .map((ov)-> (ConceptLibrary)ov).collect(Collectors.toList()));

        model.setConditions((ArrayList<TableOfVRM>) hvrm.getConditions().stream()
                .filter((con)->con.getModuleId().equals(moduleId))
                        .map((con)->(TableOfVRM)con)
                .collect(Collectors.toList()));

        model.setEvents((ArrayList<TableOfVRM>) hvrm.getEvents().stream()
                .filter((ent)->ent.getModuleId().equals(moduleId))
                .map((ent)->(TableOfVRM)ent)
                .collect(Collectors.toList()));
        
        model.setModeClass(hvrm.getModeClass());

        return model;
    }



    /**
     * 转换不同类别的VRM模型，注意是浅拷贝
     *
     * @param source 源
     * @param type   目标类型
     * @return {@link T2}
     */
    @SneakyThrows
    @Override
    public <T1 extends VariableRelationModel, T2 extends VariableRelationModel> T2 convert(T1 source, Class<T2> type) {
        T2 model = type.getDeclaredConstructor().newInstance();
        BeanUtils.copyProperties(source, model);
        return model;
    }
}
