package com.nuaa.art.vrmcheck.service.impl;

import com.nuaa.art.vrm.model.VRMOfXML;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.common.utils.OutputUtils;
import com.nuaa.art.vrmcheck.model.*;
import com.nuaa.art.vrmcheck.service.EventHandler;
import com.nuaa.art.vrmcheck.service.ModelCheckEventHandler;
import jakarta.annotation.Resource;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

@Service
public class ModelCheckEventOfXML implements ModelCheckEventHandler {
    @Resource
    EventHandler eventHandler;
    /**
     * 检查事件一致性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkEvent(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator tableIterator = vrmModel.tablesNode.elementIterator();
        while (tableIterator.hasNext()) {
            Element table = (Element) tableIterator.next();
            if (table.element("eventTable") != null) {// 事件表
                String modeClass = table.elementText("referencedStateMachine");
                ArrayList<String> modesInTable = new ArrayList<String>();
                Element eventTable = table.element("eventTable");
                Iterator rowIterator = eventTable.elementIterator();
                while (rowIterator.hasNext()) {// 获取表格中不同的模式
                    String mode = ((Element) rowIterator.next()).element("state").getText();
                    if (!modesInTable.contains(mode))
                        modesInTable.add(mode);
                }
                Iterator<String> modesInTableIterator = modesInTable.iterator();

                while (modesInTableIterator.hasNext()) {// 对同模式的事件进行一致性判断
                    String modeInTable = modesInTableIterator.next();// 获取下一个出现在表中的不同模式

                    ArrayList<String> eventForEachRow = new ArrayList<String>();// 每行的事件集合
                    ArrayList<String> behaviorForEachRow = new ArrayList<String>();// 每行的目标模式
                    ArrayList<Integer> wrongRowReqID = new ArrayList<Integer>();// 对应每行的需求编号
                    // 解析每一行事件，包括解析变量与取值、保存每一行模式、输出值、事件、事件类型、两个条件
                    rowIterator = eventTable.elementIterator();
                    while (rowIterator.hasNext()) {
                        Element row = (Element) rowIterator.next();
                        String mode = row.element("state").getText();
                        if (mode.equals(modeInTable)) {// 取出模式为modeInTable的行信息
                            String behavior = row.element("assignment").getText();// 该行输出值
                            behaviorForEachRow.add(behavior);// 该行输出值存入上层集合
                            eventForEachRow
                                    .add(row.element("event").getText().replace(" ", ""));// 该行事件存入上层集合
                            wrongRowReqID.add(Integer.valueOf(row.elementText("relateSReq")));
                        }
                    }

                    Event ep = new Event(vrmModel, eventForEachRow, behaviorForEachRow);
                    ep.parseEventIntoStates();
                    ArrayList<EventConsistencyError> eces = eventHandler.findConsistencyError(ep);
                    String variableSet = OutputUtils.getVariableSetHeader(ep.continualVariables, ep.discreteVariables);

                    // 输出出错信息
                    if (!eces.isEmpty()) {
                        String outputString = "";// 输出文本
                        errorReporter.setEventRight(false);
                        for (EventConsistencyError ece : eces) {// 遍历每一组冲突
                            errorReporter.addErrorCount();
                            //errorString = "错误" + errorCount + "-错误类型：第三范式检查，违反事件一致性";
                            outputString = "错误定位：表格" + table.attributeValue("name")
                                    + "\n错误内容：";
                            if (!modeInTable.equals(""))
                                outputString += "处于模式" + modeInTable + "下时，\n";
                            outputString += "当变量取值从下列两表前者任意一行的组合变换到后者任意一行的组合时，\n";
                            if (table.attributeValue("isOutput").equals("1"))
                                outputString += "输出变量";
                            else
                                outputString += "中间变量";
                            outputString += "同时取" + ece.assignment[0] + "和"
                                    + ece.assignment[1] + "\n" + variableSet;
                            for (ConcreteScenario cs : ece.obeyScenarios[0]) {
                                outputString += "|";
                                for (String value : cs.concreteScenario) {
                                    outputString += String.format("%-15s", value) + "|";
                                }
                                outputString += "\n";
                            }
                            outputString += "\n";
                            outputString += variableSet;
                            for (ConcreteScenario cs : ece.obeyScenarios[1]) {
                                outputString += "|";
                                for (String value : cs.concreteScenario) {
                                    outputString += String.format("%-15s", value) + "|";
                                }
                                outputString += "\n";
                            }
                            outputString = outputString.substring(0,
                                    outputString.length() - 1);

                            errorReporter.addErrorList(new CheckErrorInfo(
                                    errorReporter.getErrorCount(),
                                    CheckErrorType.EventConsistencyValue,
                                    table.attributeValue("name"),
                                    wrongRowReqID,
                                    modeClass,
                                    outputString));
                        }
                    }
                }
            }
        }
        return false;
    }


}
