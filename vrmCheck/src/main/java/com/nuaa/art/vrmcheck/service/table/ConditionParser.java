package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrm.model.ConditionItem;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.model.vrm.*;
import com.nuaa.art.vrmcheck.model.table.AndOrConditionsInformation;
import com.nuaa.art.vrmcheck.model.table.NuclearCondition;

import java.util.ArrayList;
import java.util.List;

public interface ConditionParser {
    AndOrConditionsInformation emptyInformationFactory();
    void setParentRangeAndValueOfEachRow(VRM vrmModel, TableOfVRM table, AndOrConditionsInformation ci);

    /**
     * 同源子条件表的信息，包括：
     * 1. 原子命题树
     * 2. 关键变量以及关键值，对于连续变量还包括值域
     * 可用于后一步的场景构建
     *
     * @param vrmModel  VRM模型
     * @param tableRows 表行
     */
    void praserInformationInCondtions(VRM vrmModel, List<TableRow> tableRows, AndOrConditionsInformation c);

    void sortContinualValues(ArrayList<ArrayList<String>> continualValues);

    int[] rangeDiscretization(int size, int size1, ArrayList<ArrayList<String>> continualValues, ArrayList<ArrayList<String>> discreteRanges);

    void findCriticalVariableAndKeyValues(VRM vrm, AndOrConditionsInformation cie, ConditionItem c);

    ArrayList<ArrayList<NuclearCondition>> praserConditionTree(ConditionTable second);
}
