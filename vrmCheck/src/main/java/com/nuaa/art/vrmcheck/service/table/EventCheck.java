package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;

public interface EventCheck {
    /**
     * 检查事件一致性
     *
     * @param vrmModel      VRM模型
     * @param errorReporter 错误报告器
     * @return boolean
     */
    boolean checkEventConsistency(VRM vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查模式转换一致性
     *
     * @param vrmModel      VRM模型
     * @param errorReporter 错误报告器
     * @return boolean
     */
    boolean checkModeTransConsistency(VRM vrmModel, CheckErrorReporter errorReporter);
}
