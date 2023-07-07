package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.VRMOfXML;

public interface ModelCheckOutputHandler {
    /**
     * 检查事件表输出完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkOutputIntegrityOfCondition(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查事件表输出完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkOutputIntegrityOfEvent(VRMOfXML vrmModel, CheckErrorReporter errorReporter);
}
