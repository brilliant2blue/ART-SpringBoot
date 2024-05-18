package com.nuaa.art.vrmverify.model.send;

import com.nuaa.art.vrmverify.model.explanation.Cause;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 封装返回的反例解析结果
 * @author djl
 * @date 2024-04-16
 */
@Data
@AllArgsConstructor
public class ReturnExplainResult {
    Integer type;
    String name;
    private int count;
    private List<CTLFormula> ctlFormulaList;
    private List<List<String>> highlightedPropertiesList;
    private List<VariableTable> variableTableList;
    private List<Set<Cause>> causeSetList;
}
