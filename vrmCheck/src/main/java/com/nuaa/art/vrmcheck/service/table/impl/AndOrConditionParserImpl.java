package com.nuaa.art.vrmcheck.service.table.impl;

import com.nuaa.art.vrm.common.utils.ConditionTableUtils;
import com.nuaa.art.vrm.common.utils.DataTypeUtils;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.Type;

import com.nuaa.art.vrm.model.ConditionItem;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.model.vrm.*;
import com.nuaa.art.vrmcheck.model.table.CriticalVariables;
import com.nuaa.art.vrmcheck.model.table.NuclearCondition;
import com.nuaa.art.vrmcheck.model.table.AndOrConditionsInformation;
import com.nuaa.art.vrmcheck.service.table.ConditionParser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AndOrConditionParserImpl implements ConditionParser {

    @Resource
    ConditionTableUtils tableUtils;
    @Resource
    DataTypeUtils typeUtils;

    /**
     * 用于分析条件
     *
     * @return {@link AndOrConditionsInformation}
     */
    @Override
    public AndOrConditionsInformation emptyInformationFactory(){
        AndOrConditionsInformation ci =  new AndOrConditionsInformation();
        return ci;
    }

    // 不仅可以设置离散型，也应当设置连续型， 连续型按有赋值的进行设置
    // todo 获取每行的赋值，如果后续支持表达式的处理，需要在此进行逻辑修改
    public void setParentRangeAndValueOfEachRow(VRM vrmModel, TableOfVRM table, AndOrConditionsInformation ci){
        Type thisType = typeUtils.FindVariableType(table.getRelateVar(), vrmModel.getTypes());
        // 获取每行的赋值
        ci.assignmentForEachRow = (ArrayList<String>) table.getRows().stream().map(TableRow::getAssignment).collect(Collectors.toList());
        if(typeUtils.WhetherContinuousRange(thisType)){
            ci.outputRanges = (ArrayList<String>) ci.assignmentForEachRow.clone();
        } else {
            ci.outputRanges = (ArrayList<String>) typeUtils.GetRangeList(thisType);
        }
        // 获取每行的赋值
        ci.assignmentForEachRow = (ArrayList<String>) table.getRows().stream().map(TableRow::getAssignment).collect(Collectors.toList());
    }



    /**
     * 同源子条件表的信息，包括：
     * 1. 原子命题树
     * 2. 关键变量以及关键值，对于连续变量还包括值域
     * 可用于后一步的场景构建
     *
     * @param vrmModel  VRM模型
     * @param tableRows 表行
     */
    @Override
    public void praserInformationInCondtions(VRM vrmModel, List<TableRow> tableRows, AndOrConditionsInformation c){

        for (TableRow row : tableRows) { // 获取每行的原子条件
            //System.out.println(row.toString());
            ConditionTable thisCondition = tableUtils.ConvertStringToTable(row.getDetails());
            ArrayList<ConditionItem> conditions = thisCondition.getConditionItems(); //原始的原子条件元素
            for (ConditionItem conditionItem: conditions) {
                findCriticalVariableAndKeyValues(vrmModel,c,conditionItem); // 获取关键变量、关键值
            }
            ArrayList<ArrayList<NuclearCondition>> orTree = praserConditionTree(thisCondition);
            c.nuclearTreeForEachRow.add(orTree);
        }

        c.criticalVariables.sortContinualValues();
        // 基于关键值的值域离散化
        c.variableRanges = c.criticalVariables.rangeDiscretization();

    }

    public  ArrayList<ArrayList<NuclearCondition>> praserConditionTree(ConditionTable conditionTable) {
        ArrayList<ArrayList<NuclearCondition>> orTree = new ArrayList<>();
        ArrayList<ConditionItem> conditions = conditionTable.getConditionItems(); //原始的原子条件元素
        ArrayList<ArrayList<String>> orRelation = conditionTable.getOrList(); // 每个orlist是每个原子条件在or树中的取值正反情况
        for (int i = 0; i < conditionTable.getOrNum(); i++) { // 遍历获取一组and条件
            ArrayList<NuclearCondition> andTree = new ArrayList<>();
            for(int j = 0; j < conditionTable.getAndNum(); j++) {
                String symbol = orRelation.get(j).get(i); //获取or类型
                if(symbol.equals("T")){
                    if(conditions.get(j).whetherEmpty()){
                        NuclearCondition nc = new NuclearCondition();
                        nc.setTrue();
                        andTree.add(nc);
                    } else {
                        NuclearCondition nc = new NuclearCondition(conditions.get(j));
                        andTree.add(nc);
                    }
                } else if (symbol.equals("F")) {
                    if(conditions.get(j).whetherEmpty()){
                        NuclearCondition nc = new NuclearCondition();
                        nc.setFalse();
                        andTree.add(nc);
                    } else {
                        NuclearCondition nc = new NuclearCondition(conditions.get(j));
                        String operator = nc.getOperator();
                        if (operator.equals("<"))
                            operator = ">=";
                        else if (operator.equals(">"))
                            operator = "<=";
                        else
                            operator = "!=";
                        nc.setOperator(operator);
                        andTree.add(nc);
                    }
                } else { // 解析or符号为任意的清况
                    if(i == 0 && j == 0 && conditions.get(j).whetherEmpty()){
                        NuclearCondition nc = new NuclearCondition();
                        nc.setDefault();
                        andTree.add(nc);
                        break;
                    }
                }
            }
            orTree.add(andTree);
        }
        return orTree;
    }


    /**
     * 查找关键变量和关键值
     *
     * @param vrmModel  VRM模型
     * @param c         C
     * @param condition 条件
     */
    @Override
    public void findCriticalVariableAndKeyValues(VRM vrmModel, AndOrConditionsInformation c, ConditionItem condition){
        CriticalVariables cv = c.criticalVariables;
        ConceptLibrary variable = null;
        variable = vrmModel.getInputs().stream().filter(item->item.getConceptName().equals(condition.getVar1())).findFirst().orElse(variable);
        variable = vrmModel.getTerms().stream().filter(item->item.getConceptName().equals(condition.getVar1())).findFirst().orElse(variable);
        variable = vrmModel.getOutputs().stream().filter(item->item.getConceptName().equals(condition.getVar1())).findFirst().orElse(variable);
        if(variable!= null){
            Type thisType = typeUtils.FindVariableType(variable, vrmModel.getTypes());
            if(typeUtils.WhetherContinuousRange(thisType)){ //连续型值域
                if (cv.continualVariables.contains(condition.getVar1())) {// 变量已存储
                    int index = cv.continualVariables.indexOf(condition.getVar1());
                    if (!cv.continualValues.get(index).contains(condition.getVar2())) {// 值未存储
                        ArrayList<String> thisContinualValues = cv.continualValues.get(index);
                        thisContinualValues.add(condition.getVar2());
                        cv.continualValues.set(index,thisContinualValues);
                    }
                } else {// 变量未存储
                    cv.continualVariables.add(condition.getVar1());
                    ArrayList<String> cr = new ArrayList<>(2);
                    List range = typeUtils.GetRangeList(thisType);
                    cr.add(range.get(0).toString());
                    cr.add(range.get(1).toString());
                    ArrayList<String> thisContinualValues = new ArrayList<String>();
                    thisContinualValues.add(condition.getVar2());
                    cv.continualRanges.add(cr);
                    cv.continualValues.add(thisContinualValues);
                }
            } else { //离散型值域
                if (!cv.discreteVariables.contains(condition.getVar1())) {// 变量未存储
                    cv.discreteVariables.add(condition.getVar1());
                    ArrayList<String> thisDiscreteRanges = (ArrayList<String>) typeUtils.GetRangeList(thisType);
                    cv.discreteRanges.add(thisDiscreteRanges);
                }
            }
        } else { // 是模式的情况
            ModeClassOfVRM mc = null;
            mc = vrmModel.getModeClass().stream()
                    .filter(item-> item.getModeClass().getModeClassName().equals(condition.getVar1()))
                    .findFirst().orElse(mc);
            if(!cv.discreteVariables.contains(condition.getVar1())){
                ArrayList<String> thisDiscreteRanges = new ArrayList<String>();
                if (mc != null) {
                    thisDiscreteRanges.addAll(mc.getModes().stream().map(Mode::getModeName).toList());
                }
                cv.discreteRanges.add(thisDiscreteRanges);
            }
        }
    }

    // 将收集的每个关键连续变量的关键值从小到大排序。
    @Override
    public void sortContinualValues( ArrayList<ArrayList<String>> continualValues) {
        for (int i = 0; i < continualValues.size(); i++) {
            ArrayList<String> thisContinualValues = continualValues.get(i);
            thisContinualValues.sort((o1, o2) -> {
                Double fo1 = Double.parseDouble(o1);
                Double fo2 = Double.parseDouble(o2);
                if (fo1 > fo2)
                    return 1;
                else if (fo1.equals(fo2))
                    return 0;
                else
                    return -1;
            });
            continualValues.set(i, thisContinualValues);
        }
    }

    /**
     * 值域离散化，为所有关键变量构建新值域
     *
     * @param discreteNumber         离散型变量个数
     * @param continualNumber        连续型变量个数
     * @param continualValues        连续型变量关键值
     * @param discreteRanges         离散变量值域
     * @return {@link int[]}
     */
    public int[] rangeDiscretization(int continualNumber, int discreteNumber, ArrayList<ArrayList<String>> continualValues, ArrayList<ArrayList<String>> discreteRanges){
        int variableNumber = continualNumber + discreteNumber;
        int[] variableRanges = new int[variableNumber];
        for (int i = 0; i < variableNumber; i++) {
            if (i < continualNumber) {
                variableRanges[i] = 2 * continualValues.get(i).size() + 1;
            } else {
                variableRanges[i] = discreteRanges.get(i - continualNumber).size();
            }
        }
        return variableRanges;
    }

}
