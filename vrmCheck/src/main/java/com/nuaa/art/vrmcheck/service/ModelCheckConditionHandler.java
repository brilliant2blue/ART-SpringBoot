package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.VRMOfXML;

public interface ModelCheckConditionHandler {
    /**
     * 检查条件一致性和条件完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkCondition(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

}
