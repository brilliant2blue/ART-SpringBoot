package com.nuaa.art.vrmcheck.service.obj;

import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;

public interface EventCheck {
    /**
     * 检查事件一致性
     *
     * @param vrmModel      VRM模型
     * @param errorReporter 错误报告器
     * @return boolean
     */
    boolean checkEventConsistency(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查模式转换一致性
     *
     * @param vrmModel      VRM模型
     * @param errorReporter 错误报告器
     * @return boolean
     */
    boolean checkModeTransConsistency(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);
}
