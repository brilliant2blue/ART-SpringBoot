package com.nuaa.art.vrmcheck.service.obj;

import com.nuaa.art.vrm.model.VRMOfXML;
import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;

public interface BasicCheck {
    /**
     * 检查输入变量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查类型定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkTypeBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查常量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkConstantBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查变量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkVariableBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查模式集定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkModeClassBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查表格函数定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkTableBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter);
}
