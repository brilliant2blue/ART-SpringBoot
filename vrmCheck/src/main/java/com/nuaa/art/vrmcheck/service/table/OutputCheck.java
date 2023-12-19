package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;

/**
 * 输出完整性检查
 *
 * @author konsin
 * @date 2023/09/12
 */
public interface OutputCheck {
    /**
     * 检查条件表输出完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkOutputIntegrityOfCondition(VRM vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查事件表输出完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkOutputIntegrityOfEvent(VRM vrmModel, CheckErrorReporter errorReporter);
}
