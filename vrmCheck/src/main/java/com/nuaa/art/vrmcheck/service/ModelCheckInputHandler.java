package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.VRMOfXML;

public interface ModelCheckInputHandler {
    /**
     * 检查条件表输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputIntegrityOfCondition(VRMOfXML vrmModel, CheckErrorReporter errorReporter);


    /**
     * 检查事件表输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputIntegrityOfEvent(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查模式转换表的输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputIntegrityOfModeTrans(VRMOfXML vrmModel, CheckErrorReporter errorReporter);
}
