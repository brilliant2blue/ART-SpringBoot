package com.nuaa.art.vrmcheck.service.obj;

import com.nuaa.art.vrm.model.*;
import com.nuaa.art.vrmcheck.model.ScenarioCorpusCoder;
import com.nuaa.art.vrmcheck.model.obj.ConditionsInformation;
import com.nuaa.art.vrmcheck.model.obj.NuclearCondition;

import java.util.ArrayList;
import java.util.List;

public interface ConditionPraser {
    ConditionsInformation emptyInformationFactory();
    void setParentRangeAndValueOfEachRow(VariableRealationModel vrmModel, TableOfVRM table, ConditionsInformation ci);

    /**
     * 同源子条件表的信息，包括：
     * 1. 原子命题树
     * 2. 关键变量以及关键值，对于连续变量还包括值域
     * 可用于后一步的场景构建
     *
     * @param vrmModel  VRM模型
     * @param tableRows 表行
     */
    void praserInformationInCondtions(VariableRealationModel vrmModel, List<TableRow> tableRows, ConditionsInformation c);

    void sortContinualValues(ArrayList<ArrayList<String>> continualValues);

    int[] rangeDiscretization(int size, int size1, ArrayList<ArrayList<String>> continualValues, ArrayList<ArrayList<String>> discreteRanges);

    void findCriticalVariableAndKeyValues(VariableRealationModel vrm, ConditionsInformation cie, ConditionItem c);

    ArrayList<ArrayList<NuclearCondition>> praserConditionTree(ConditionTable second);
}
