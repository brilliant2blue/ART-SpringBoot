package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;

/**
 * 输入完整性检查
 *
 * @author konsin
 * @date 2023/09/12
 */
public interface InputCheck {
    /**
     * 检查条件表输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputIntegrityOfCondition(VRM vrmModel, CheckErrorReporter errorReporter);


    /**
     * 检查事件表输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputIntegrityOfEvent(VRM vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查模式转换表的输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputIntegrityOfModeTrans(VRM vrmModel, CheckErrorReporter errorReporter);
}
