package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.model.VRMOfXML;

public interface ModelCheckEventHandler {
    /**
     * 检查事件一致性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkEvent(VRMOfXML vrmModel, CheckErrorReporter errorReporter);


}
