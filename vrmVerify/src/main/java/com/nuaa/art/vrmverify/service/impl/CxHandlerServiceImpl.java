package com.nuaa.art.vrmverify.service.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrmverify.common.Msg;
import com.nuaa.art.vrmverify.common.utils.CTLParseUtils;
import com.nuaa.art.vrmverify.handler.CxExplanationHandler;
import com.nuaa.art.vrmverify.handler.CxVisualizationHandler;
import com.nuaa.art.vrmverify.model.Counterexample;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import com.nuaa.art.vrmverify.service.CxHandlerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 反例处理
 * @author djl
 * @date 2024-04-02
 */
@Service
public class CxHandlerServiceImpl implements CxHandlerService {

    /**
     * 计算得到变量表
     * @param verifyResult
     * @param propertyIndex
     * @return
     */
    @Override
    public VariableTable computeVariableTable(VerifyResult verifyResult, int propertyIndex) {
        int propertyCount = verifyResult.getPropertyCount();
        if(propertyCount == 0)
            return null;
        try {
            if(propertyIndex >= propertyCount || propertyIndex < 0)
                throw new RuntimeException(Msg.INDEX_ERROR);
            List<Counterexample> cxList = verifyResult.getCxList();
            Counterexample cx = cxList.get(propertyIndex);
            if(cx.isPassed())
                return null;
            return CxVisualizationHandler.computeVariableTable(cx);
        }
        catch (Exception e){
            LogUtils.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 解释反例
     * @param trace
     * @param f
     * @return
     */
    public Set<Cause> explainCx(VariableTable trace, CTLFormula f){
        try {
            return CxExplanationHandler.computeCauseSet(trace, 0, f, false);
        }
        catch (Exception e){
            LogUtils.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * 解析得 CTL 公式
     * @param verifyResult
     * @param propertyIndex
     * @param isSimplified
     * @return
     */
    @Override
    public CTLFormula parseCTLFormula(VerifyResult verifyResult, int propertyIndex, boolean isSimplified) {
        int propertyCount = verifyResult.getPropertyCount();
        try {
            if(propertyIndex >= propertyCount || propertyIndex < 0)
                throw new RuntimeException(Msg.INDEX_ERROR);
            String property = verifyResult.getCxList().get(propertyIndex).getProperty();
            if(isSimplified)
                return CTLParseUtils.parseCTLStrAndSimplify(property);
            else
                return CTLParseUtils.parseCTLStr(property);
        }
        catch (Exception e){
            LogUtils.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
