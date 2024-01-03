package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrm.model.VariableRelationModel;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;

public interface BasicCheck<T extends VariableRelationModel> {
    /**
     * 检查输入变量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkInputBasic(T vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查类型定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkTypeBasic(T vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查常量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkConstantBasic(T vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查变量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkVariableBasic(T vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查模式集定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkModeClassBasic(T vrmModel, CheckErrorReporter errorReporter);

    /**
     * 检查表格函数定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    boolean checkTableBasic(T vrmModel, CheckErrorReporter errorReporter);
}
