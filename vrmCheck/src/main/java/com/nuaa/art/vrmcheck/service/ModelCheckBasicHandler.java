package com.nuaa.art.vrmcheck.service;

import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.VRMOfXML;

/**
 * 模型约束分析
 *
 * @author konsin
 * {@code @date} 2023/06/13
 */
public interface ModelCheckBasicHandler {
    /**
     * 检查输入变量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查类型定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkTypeBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查常量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkConstantBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查变量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkVariableBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查模式集定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkModeClassBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查表格函数定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkTableBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter);

}
