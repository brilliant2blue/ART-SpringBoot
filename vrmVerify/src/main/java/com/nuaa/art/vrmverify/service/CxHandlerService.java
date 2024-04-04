package com.nuaa.art.vrmverify.service;

import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;

import java.util.Set;

/**
 * 反例处理
 * @author djl
 * @date 2024-04-02
 */
public interface CxHandlerService {

    /**
     * 计算得到变量表
     * @param verifyResult
     * @param propertyIndex
     * @return
     */
    public VariableTable computeVariableTable(VerifyResult verifyResult, int propertyIndex);

    /**
     * 解释反例
     * @param trace
     * @param f
     * @return
     */
    public Set<Cause> explainCx(VariableTable trace, CTLFormula f);

    /**
     * 解析得 CTL 公式
     * @param verifyResult
     * @param propertyIndex
     * @param isSimplified
     * @return
     */
    public CTLFormula parseCTLFormula(VerifyResult verifyResult, int propertyIndex, boolean isSimplified);
}
