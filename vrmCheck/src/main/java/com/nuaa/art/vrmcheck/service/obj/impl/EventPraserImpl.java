package com.nuaa.art.vrmcheck.service.obj.impl;

import com.nuaa.art.vrm.common.utils.EventTableUtils;
import com.nuaa.art.vrm.model.*;
import com.nuaa.art.vrmcheck.model.obj.ConditionsInformation;
import com.nuaa.art.vrmcheck.model.obj.NuclearCondition;
import com.nuaa.art.vrmcheck.model.obj.EventsInformation;
import com.nuaa.art.vrmcheck.service.obj.ConditionPraser;
import com.nuaa.art.vrmcheck.service.obj.EventPraser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class EventPraserImpl implements EventPraser {

    @Resource
    EventTableUtils tableUtils;

    @Resource
    ConditionPraser conditionPraser;

    @Override
    public EventsInformation emptyEventsInformationFactory(){
        return new EventsInformation();
    }


    @Override
    public void praserInformationInTables(VariableRealationModel vrm, List<TableRow> tableRows, EventsInformation ei){
        ei.nuclearTreeForEachRow = new ArrayList<ArrayList<ArrayList<ArrayList<NuclearCondition>>[]>>();
        ei.continualVariables = new ArrayList<String>();
        ei.discreteVariables = new ArrayList<String>();
        ei.continualValues = new ArrayList<ArrayList<String>>();
        ei.discreteRanges = new ArrayList<ArrayList<String>>();
        ei.scenarioSetPair = new ArrayList<ArrayList<Long>[]>();
        ei.continualRanges = new ArrayList<ArrayList<String>>();
        ei.eventOperatorsForEachRow = new ArrayList<ArrayList<String>>();
        ei.guardOperatorsForEachRow = new ArrayList<ArrayList<String>>();
        ei.twoScenarioSetForEachRow = new ArrayList<ArrayList<ArrayList<Long>[]>>();

        ei.assignmentForEachRow = (ArrayList<String>) tableRows.stream().map(TableRow::getAssignment).collect(Collectors.toList());

        for(TableRow row : tableRows){
            if(row.getDetails().isBlank() || row.getDetails().equalsIgnoreCase("never")){
                continue;
            }
            EventTable table =  tableUtils.ConvertStringToTable(row.getDetails());
            if(table.getAndNum() != 0){
                ArrayList< ArrayList<ArrayList<NuclearCondition>>[] > andEventsForThisRow = new ArrayList<ArrayList<ArrayList<NuclearCondition>>[]>();
                // 该行各个子事件的事件类型
                ArrayList<String> eventOp = new ArrayList<>();
                ArrayList<String> guardOp = new ArrayList<>();
                for (EventItem event: table.getEvents()) {
                    eventOp.add(event.getEventOperator());
                    guardOp.add(event.getGuardOperator().isBlank()? "when":event.getGuardOperator());
                    //获取事件内条件信息
                    ConditionsInformation cie = praserSubConditionInformation(vrm, event.getEventCondition(), event.getGuardCondition());
                    ArrayList<ArrayList<NuclearCondition>>[] twoNuclearTreesForThisAndEvnet = new ArrayList[2];
                    for (int i = 0; i < 2; i++) {
                        twoNuclearTreesForThisAndEvnet[i] = cie.nuclearTreeForEachRow.get(i);
                    }
                    andEventsForThisRow.add(twoNuclearTreesForThisAndEvnet);

                    // 将子条件的属性并入事件属性中
                    for (int i = 0; i < cie.continualVariables.size(); i++) {
                        if (!ei.continualVariables.contains(cie.continualVariables.get(i))) {
                            ei.continualVariables.add(cie.continualVariables.get(i));
                            ei.continualValues.add(cie.continualValues.get(i));
                            ei.continualRanges.add(cie.continualRanges.get(i));
                        } else {
                            int variableIndex = ei.continualVariables.indexOf(cie.continualVariables.get(i));
                            ArrayList<String> thisContinualValues = cie.continualValues.get(i);
                            ArrayList<String> continualValuesOfVariable = ei.continualValues.get(variableIndex);
                            for (String continualValue : thisContinualValues) {
                                if (!continualValuesOfVariable.contains(continualValue))
                                    continualValuesOfVariable.add(continualValue);
                            }
                            ei.continualValues.set(variableIndex, continualValuesOfVariable);
                        }
                    }
                    for (int i = 0; i < cie.discreteVariables.size(); i++) {
                        if (!ei.discreteVariables.contains(cie.discreteVariables.get(i))) {
                            ei.discreteVariables.add(cie.discreteVariables.get(i));
                            ei.discreteRanges.add(cie.discreteRanges.get(i));
                        } else {
                            int variableIndex = ei.discreteVariables.indexOf(cie.discreteVariables.get(i));
                            ArrayList<String> thisDiscreteRanges = cie.discreteRanges.get(i);
                            ArrayList<String> discreteValuesOfVariable = ei.discreteRanges.get(variableIndex);
                            for (String discreteRange : thisDiscreteRanges) {
                                if (!discreteValuesOfVariable.contains(discreteRange))
                                    discreteValuesOfVariable.add(discreteRange);
                            }
                            ei.discreteRanges.set(variableIndex, discreteValuesOfVariable);
                        }
                    }
                }
                ei.nuclearTreeForEachRow.add(andEventsForThisRow);
                ei.eventOperatorsForEachRow.add(eventOp);
                ei.guardOperatorsForEachRow.add(guardOp);
            }
        }

        conditionPraser.sortContinualValues(ei.continualValues);
        createTrueAndFalseRowFormContraryEvent(ei);
        ei.variableRanges = conditionPraser.rangeDiscretization(ei.continualVariables.size(), ei.discreteVariables.size(), ei.continualValues, ei.discreteRanges);
    }

    public ConditionsInformation praserSubConditionInformation(VariableRealationModel vrm, ConditionTable first, ConditionTable second){
        ConditionsInformation cie = conditionPraser.emptyInformationFactory();
        cie.nuclearTreeForEachRow.add(conditionPraser.praserConditionTree(first));
        cie.nuclearTreeForEachRow.add(conditionPraser.praserConditionTree(second));

        cie.continualVariables = new ArrayList<>();
        cie.discreteVariables = new ArrayList<>();
        cie.continualValues = new ArrayList<>();
        cie.discreteRanges = new ArrayList<>();
        cie.continualRanges = new ArrayList<>();
        for(ConditionItem c: first.getConditionItems())
            conditionPraser.findCriticalVariableAndKeyValues(vrm,cie, c);
        for(ConditionItem c: second.getConditionItems())
            conditionPraser.findCriticalVariableAndKeyValues(vrm,cie, c);

        // 基于关键值的值域离散化
        conditionPraser.sortContinualValues(cie.continualValues);
        cie.variableRanges = conditionPraser.rangeDiscretization(cie.continualVariables.size(), cie.discreteVariables.size(), cie.continualValues, cie.discreteRanges);
                return cie;
    }

    /**
     * 将事件内的每个@C类子事件分解为各一个@T类和@F类，并复制一次该事件
     *
     * @param ei EI
     */
    public void  createTrueAndFalseRowFormContraryEvent(EventsInformation ei) {
        for (int i = 0; i < ei.eventOperatorsForEachRow.size(); i++) {// 遍历各行事件类型，对@C事件，将其信息拷贝为两份，分别设为@T和@F，进行保存
            ArrayList<String> eventOpList = ei.eventOperatorsForEachRow.get(i);// 本行各AND事件的事件类型
            ArrayList<String> guardOpList = ei.guardOperatorsForEachRow.get(i);// 本行各AND事件的事件类型
            ArrayList<Integer> indexOfAndAtCEvent = new ArrayList<Integer>();// 本行AND事件中所有@C事件索引值
            for (int j = 0; j < eventOpList.size(); j++) {// 遍历各AND事件的事件类型
                String eventOp = eventOpList.get(j);// 此AND事件的事件类型
                if (eventOp.equalsIgnoreCase("@C")) {
                    indexOfAndAtCEvent.add(j);// 若为@C，将此索引值加入集合
                }
            }
            for (int j = 0; j < Math.pow(2, indexOfAndAtCEvent.size()); j++) {// 对于每个@C的子事件，保持其他不变拷贝为两份，将自身改为@F和@T，一共拷贝为2的x次方份，x为@C的子事件数量
                if (j == 0) {// 对于拷贝源，保持其在集合中，并将其所有@C改为@T
                    for (int index : indexOfAndAtCEvent) {
                        ArrayList<String> thisEventOpList = ei.eventOperatorsForEachRow.get(i);// 该行各AND事件的事件类型
                        String thisEventOp = thisEventOpList.get(index);// 索引处AND事件的事件类型
                        thisEventOp = "@T";// 改为@T
                        thisEventOpList.set(index, thisEventOp);// 存回
                        ei.eventOperatorsForEachRow.set(i, thisEventOpList);// 存回
                    }
                } else {
                    // 新添行各AND事件的事件类型
                    ArrayList<String> newEventOpList = new ArrayList<String>();
                    ArrayList<String> newGuardOpList = new ArrayList<String>();
                    ArrayList<ArrayList<ArrayList<NuclearCondition>>[]> newNuclearTree = new ArrayList<ArrayList<ArrayList<NuclearCondition>>[]>();// 新添行各AND事件的两个条件

                    // 将j存储为二进制字符串，每一位指示着一个@C子事件的改写类型。 通过每一位的编码判断各@C的AND事件应拷贝为@T或@F
                    String binary = Integer.toBinaryString(j);
                    if (binary.length() < indexOfAndAtCEvent.size()) {// 令binary位数与@C的子事件个数相同，使其一一对应
                        int subLength = indexOfAndAtCEvent.size() - binary.length();
                        for (int k = 0; k < subLength; k++) {
                            binary = "0" + binary;
                        }
                    }
                    for (int k = 0; k < eventOpList.size(); k++) {
                        if (!indexOfAndAtCEvent.contains(Integer.valueOf(k))) {// 对于不为@C的子事件，直接复制
                            // 新添行中第k个子事件的事件类型，存入新添行集合
                            newEventOpList.add(eventOpList.get(k));
                        } else {// 对于为@C的子事件，判断此时j的二进制编码，对应索引值为0的子事件复制为@T，否则为@F
                            // 新添行中第k个AND事件的事件类型
                            newEventOpList.add((binary.charAt(indexOfAndAtCEvent.indexOf(k)) == '0')? "@T": "@F");
                        }
                        newGuardOpList.add(guardOpList.get(k));// 复制时序谓词guard
                        // 新添行中第k个AND事件的两个条件
                        ArrayList<ArrayList<NuclearCondition>>[] nuclearTrees = new ArrayList[2];
                        nuclearTrees[0] = ei.nuclearTreeForEachRow.get(i).get(k)[0];
                        nuclearTrees[1] = ei.nuclearTreeForEachRow.get(i).get(k)[1];
                        newNuclearTree.add(nuclearTrees);// 存入新添行集合
                    }
                    String newBehavior = ei.assignmentForEachRow.get(i);// 新添行的目标模式
                    ei.eventOperatorsForEachRow.add(newEventOpList);
                    ei.eventOperatorsForEachRow.add(newGuardOpList);
                    ei.nuclearTreeForEachRow.add(newNuclearTree);
                    ei.assignmentForEachRow.add(newBehavior);
                }
            }
        }
    }
}
