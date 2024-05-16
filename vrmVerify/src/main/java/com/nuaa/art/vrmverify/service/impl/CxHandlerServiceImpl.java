package com.nuaa.art.vrmverify.service.impl;

import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.CTLParseUtils;
import com.nuaa.art.vrmverify.common.utils.TreeTraverseUtils;
import com.nuaa.art.vrmverify.handler.CxExplanationHandler;
import com.nuaa.art.vrmverify.handler.CxVisualizationHandler;
import com.nuaa.art.vrmverify.model.Counterexample;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import com.nuaa.art.vrmverify.service.CxHandlerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 反例处理
 * @author djl
 * @date 2024-04-02
 */
@Service
public class CxHandlerServiceImpl implements CxHandlerService {

    /**
     * 计算得到变量表（单个属性）
     * @param verifyResult
     * @param propertyIndex
     * @return
     */
    @Override
    public VariableTable computeVariableTable(VerifyResult verifyResult, int propertyIndex) {
        int propertyCount = verifyResult.getPropertyCount();
        if(propertyCount == 0)
            return null;
        if(propertyIndex >= propertyCount || propertyIndex < 0)
            throw new RuntimeException(Msg.INDEX_ERROR);
        List<Counterexample> cxList = verifyResult.getCxList();
        Counterexample cx = cxList.get(propertyIndex);
        if(cx.isPassed())
            return null;
        return CxVisualizationHandler.computeVariableTable(cx);
    }

    /**
     * 计算得到变量表（多个属性）
     * @param verifyResult
     * @return
     */
    @Override
    public List<VariableTable> computeVariableTables(VerifyResult verifyResult) {
        int propertyCount = verifyResult.getPropertyCount();
        if(propertyCount == 0)
            return null;
        List<Counterexample> cxList = verifyResult.getCxList();
        List<VariableTable> variableTableList = new ArrayList<>();
        for (int i = 0; i < propertyCount; i++) {
            Counterexample cx = cxList.get(i);
            if(!cx.isPassed() && cx.getAssignments() != null && !cx.getAssignments().isEmpty()){
                VariableTable variableTable = CxVisualizationHandler.computeVariableTable(cx);
                variableTableList.add(variableTable);
            }
        }
        return variableTableList;
    }

    /**
     * 解释反例
     * @param vt
     * @param f
     * @return
     */
    public Set<Cause> explainCx(VariableTable vt, CTLFormula f){
        return CxExplanationHandler.computeCauseSet(vt, 0, f, false);
    }


    /**
     * 解析得 CTL 公式
     * @param propertyStr
     * @param isSimplified
     * @return
     */
    @Override
    public CTLFormula parseCTLFormula(String propertyStr, Map<String, List<String>> values, boolean isSimplified) {
        CTLFormula ctlFormula;
        if(isSimplified)
            ctlFormula = CTLParseUtils.parseCTLStrAndSimplify(propertyStr);
        else
            ctlFormula = CTLParseUtils.parseCTLStr(propertyStr);
        TreeTraverseUtils.traverseToModifyFormula(ctlFormula, values);
        return ctlFormula;
    }

    /**
     * 计算公式中各个元素在每一反例步上的值
     * @param f
     * @param vt
     */
    @Override
    public void computeFormulaValues(CTLFormula f, VariableTable vt) {
        CxExplanationHandler.computeFormulaValues(f, vt);
    }

    /**
     * 生成html形式的属性视图
     * @param f
     * @param causeSet
     * @return
     */
    @Override
    public List<String> genHighlightedProperty(CTLFormula f, Set<Cause> causeSet) {
        return CxVisualizationHandler.genHighlightedProperty(f, causeSet);
    }
}
