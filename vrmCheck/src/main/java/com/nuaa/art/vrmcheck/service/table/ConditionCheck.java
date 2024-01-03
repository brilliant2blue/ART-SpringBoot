package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;

public interface ConditionCheck {
    /**
     * 检查条件完整性和一致性
     *
     * @param vrmModel      VRM模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    public boolean checkConditionIntegrityAndConsistency(VRM vrmModel, CheckErrorReporter errorReporter);
}
