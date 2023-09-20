package com.nuaa.art.vrmcheck.service.obj;

import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;

public interface ConditionCheck {
    /**
     * 检查条件完整性和一致性
     *
     * @param vrmModel      VRM模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    public boolean checkConditionIntegrityAndConsistency(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);
}
