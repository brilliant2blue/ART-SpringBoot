package com.nuaa.art.vrmcheck.service.impl;

import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrm.model.model.VRMOfXML;
import com.nuaa.art.vrmcheck.common.utils.OutputUtils;
import com.nuaa.art.vrmcheck.model.*;
import com.nuaa.art.vrmcheck.service.ConditionHandler;
import com.nuaa.art.vrmcheck.service.ModelCheckConditionHandler;
import jakarta.annotation.Resource;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

@Service
public class ModelCheckConditionOfXML implements ModelCheckConditionHandler {
    @Resource
    ConditionHandler conditionHandler;

    /**
     * 检查条件一致性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkCondition(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator tableIterator = vrmModel.tablesNode.elementIterator();
        while (tableIterator.hasNext()) {
            Element table = (Element) tableIterator.next();
            if (table.element("conditionTable") != null) {// 条件表
                String modeClass = table.elementText("referencedStateMachine");
                ArrayList<String> modesInTable = new ArrayList<String>();
                Element conditionTable = table.element("conditionTable");
                Iterator rowIterator = conditionTable.elementIterator();
                String range = table.elementText("range");
                String[] ranges = range.split(",");
                ArrayList<String> outputRanges = new ArrayList<String>(
                        Arrays.asList(ranges));
                while (rowIterator.hasNext()) {// 获取表格中不同的模式
                    String mode = ((Element) rowIterator.next()).element("state").getText();
                    if (!modesInTable.contains(mode))
                        modesInTable.add(mode);
                }
                Iterator<String> modesInTableIterator = modesInTable.iterator();
                ArrayList<Integer> wrongRowReqID = new ArrayList<Integer>();// 对应每行的需求编号
                while (modesInTableIterator.hasNext()) {// 对同模式的条件进行一致性完整性判断
                    String modeInTable = modesInTableIterator.next();// 获取下一个出现在表中的不同模式

                    ArrayList<String> conditionForEachRow = new ArrayList<String>();// 每行的条件集合
                    ArrayList<String> behaviorForEachRow = new ArrayList<String>();// 每行的目标模式

                    ArrayList<String> continualVariables = new ArrayList<String>();// 条件中出现的连续型变量
                    ArrayList<String> discreteVariables = new ArrayList<String>();// 条件中出现的离散型变量
                    ArrayList<ArrayList<String>> continualValues = new ArrayList<ArrayList<String>>();// 连续型变量在事件中出现的比较值，索引值与continualVariables一一对应
                    ArrayList<ArrayList<String>> discreteRanges = new ArrayList<ArrayList<String>>();// 离散型变量对应的值域，索引值与discreteVariables一一对应

                    // 解析每一行条件，包括解析变量与取值、保存每一行模式、输出值、条件
                    rowIterator = conditionTable.elementIterator();
                    while (rowIterator.hasNext()) {
                        Element row = (Element) rowIterator.next();
                        String mode = row.element("state").getText();
                        if (mode.equals(modeInTable)) {// 取出源模式为modeInTable的行信息
                            String behavior = row.element("assignment").getText();// 该行目标模式
                            String condition = row.element("condition").getText()
                                    .replace(" ", "");
                            behaviorForEachRow.add(behavior);// 该行目标模式存入上层集合
                            conditionForEachRow.add(condition);// 该行事件存入上层集合
                            wrongRowReqID.add(Integer.valueOf(row.elementText("relateSReq")));
                        }
                    }

                    Condition rcp = new Condition(
                            vrmModel, conditionForEachRow, behaviorForEachRow, outputRanges);
                    rcp.parseConditionIntoScenarios();
                    String variableSet = OutputUtils.getVariableSetHeader(rcp.continualVariables, rcp.discreteVariables);;

                    ArrayList<ConditionConsistencyError> cceList = conditionHandler.findConsistencyError(rcp);

                    ConditionIntegrityError cie = conditionHandler.fineIntegrityError(rcp);
                    // 打印完整性错误信息
                    if (cie.isTrueNotExist) {
                        ConditionErrorRefresh(errorReporter);
                        String outputString = "";// 输出文本
                        outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：";
                        if (!modeInTable.equals(""))
                            outputString += "处于模式" + modeInTable + "下时，\n";
                        outputString += "表格中只存在永假条件";
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.ConditionIntegrityOnFalse,
                                table.attributeValue("name"),
                                wrongRowReqID, modeClass, outputString));
                    } else if (cie.lostScenarios.size() != 0) {
                        ConditionErrorRefresh(errorReporter);
                        String outputString = "";// 输出文本
                        outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：";
                        if (!modeInTable.equals(""))
                            outputString += "处于模式" + modeInTable + "下时，\n";
                        outputString += "当变量取值为下表任意一行的组合时";
                        if (table.attributeValue("isOutput").equals("1"))
                            outputString += "输出变量";
                        else
                            outputString += "中间变量";
                        outputString += "无值\n" + variableSet;
                        for (ConcreteScenario cs : cie.lostScenarios) {
                            outputString += "|";
                            for (String value : cs.concreteScenario) {
                                outputString += String.format("%-15s", value) + "|";
                            }
                            outputString += "\n";
                        }
                        outputString = outputString.substring(0, outputString.length() - 1);
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.ConditionIntegrityValue,
                                table.attributeValue("name"),
                                wrongRowReqID, modeClass, outputString));
                    }

                    // 打印一致性错误信息
                    if (!cceList.isEmpty()) {
                        if (cceList.get(0).isTrueTooMuch) {
                            ConditionErrorRefresh(errorReporter);
                            String outputString = "";// 输出文本
                            outputString = "错误定位：表格" + table.attributeValue("name")
                                    + "\n错误内容：";
                            if (!modeInTable.equals(""))
                                outputString += "处于模式" + modeInTable + "下时，\n";
                            outputString += "表格中有多个输出值冲突的永真条件";
                            errorReporter.addErrorList(new CheckErrorInfo(
                                    errorReporter.getErrorCount(),
                                    CheckErrorType.ConditionConsistencyTrue,
                                    table.attributeValue("name"),
                                    wrongRowReqID, modeClass, outputString));
                        } else {
                            ConditionErrorRefresh(errorReporter);

                            String outputString = "";// 输出文本
                            outputString = "错误定位：表格" + table.attributeValue("name")
                                    + "\n错误内容：";
                            if (!modeInTable.equals(""))
                                outputString += "处于模式" + modeInTable + "下时，\n";
                            outputString += "当变量取值为下表任意一行的组合时";
                            if (table.attributeValue("isOutput").equals("1"))
                                outputString += "输出变量";
                            else
                                outputString += "中间变量";
                            outputString += "同时取每行后方的多个不同赋值" + "\n" + variableSet;
                            for (ConditionConsistencyError cce : cceList) {// 遍历每一组冲突
                                ConcreteScenario cs = cce.obeyScenarios;
                                outputString += "|";
                                for (String value : cs.concreteScenario) {
                                    outputString += String.format("%-15s", value) + "|";
                                }
                                outputString += "--->";
                                for (String output : cce.assignment) {
                                    outputString += output + ",";
                                }
                                outputString = outputString.substring(0,
                                        outputString.length() - 1);
                                outputString += "\n";
                            }
                            outputString = outputString.substring(0,
                                    outputString.length() - 1);
                            errorReporter.addErrorList(new CheckErrorInfo(
                                    errorReporter.getErrorCount(),
                                    CheckErrorType.ConditionConsistencyValue,
                                    table.attributeValue("name"),
                                    wrongRowReqID, modeClass, outputString));
                        }
                    }
                }
            }
        }

        return false;
    }

    void ConditionErrorRefresh(CheckErrorReporter errorReporter) {
        errorReporter.setConditionRight(false);
        errorReporter.addErrorCount();
    }
}
