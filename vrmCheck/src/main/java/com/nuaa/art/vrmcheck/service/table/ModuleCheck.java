package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;

public interface ModuleCheck {
    /**
     * 检查模块完整性和一致性
     * 模块完整性：
     *  有效输入变量必须享有输入端口。
     *  有效中间变量必须享有输入端口和输出端口。
     *  有效输出变量必须享有输出端口。
     *
     * 模块一致性：
     *  有效中间变量的输出端口唯一
     *  有效输出变量的输出端口唯一
     *
     * @param vrm      变量关系模型
     * @param reporter 错误报告记录
     */
    void checkModuleIntegrityAndConsistency(HVRM vrm, CheckErrorReporter reporter);

}
