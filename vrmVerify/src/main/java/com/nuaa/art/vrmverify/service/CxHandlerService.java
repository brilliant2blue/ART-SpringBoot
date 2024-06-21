package com.nuaa.art.vrmverify.service;

import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 反例处理
 * @author djl
 * @date 2024-04-02
 */
public interface CxHandlerService {

    /**
     * 计算得到变量表（单个属性）
     * @param verifyResult
     * @param propertyIndex
     * @return
     */
    public VariableTable computeVariableTable(VerifyResult verifyResult, int propertyIndex);

    /**
     * 计算得到变量表（多个属性）
     * @param verifyResult
     * @return
     */
    public List<VariableTable> computeVariableTables(VerifyResult verifyResult);

    /**
     * 解释反例
     * @param vt
     * @param f
     * @return
     */
    public Set<Cause> explainCx(VariableTable vt, CTLFormula f);

    /**
     * 解析并纠正得 CTL 公式
     * @param propertyStr
     * @param values
     * @param isSimplified
     * @return
     */
    public CTLFormula parseCTLFormula(String propertyStr, Map<String, List<String>> values, boolean isSimplified);

    /**
     * 计算公式中每个元素在每一反例步上的值
     * @param f
     * @param vt
     */
    public void computeFormulaValues(CTLFormula f, VariableTable vt);

    /**
     * 生成html形式的属性视图
     * @param f
     * @param causeSet
     * @return
     */
    public List<String> genHighlightedProperty(CTLFormula f, Set<Cause> causeSet);
}
