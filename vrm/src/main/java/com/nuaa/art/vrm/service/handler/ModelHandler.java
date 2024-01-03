package com.nuaa.art.vrm.service.handler;

import com.nuaa.art.vrm.model.VariableRelationModel;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.vrm.VRM;

import java.util.List;

public interface ModelHandler {
    /**
     * 从具有层次化的模型中生成数个不具备层次化特征的子模型。
     *
     * @param hvrm Hvrm
     * @return {@link List}<{@link T2}>
     */
    <T1 extends HVRM, T2 extends VRM> List<T2> getModuleModels(T1 hvrm, List<T2> models, Class<T2> t2Class);


    /**
     * 转换不同类别的VRM模型，注意是浅拷贝
     *
     * @param source 源
     * @param type 目标类型
     * @return {@link T2}
     */
    <T1 extends VariableRelationModel, T2 extends VariableRelationModel> T2 convert(T1 source, Class<T2> type);
}
