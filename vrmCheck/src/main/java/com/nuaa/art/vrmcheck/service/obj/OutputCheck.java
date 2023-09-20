package com.nuaa.art.vrmcheck.service.obj;

import com.nuaa.art.vrm.model.VRMOfXML;
import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;

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
    boolean checkOutputIntegrityOfCondition(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查事件表输出完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkOutputIntegrityOfEvent(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);
}
