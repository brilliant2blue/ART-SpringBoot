package com.nuaa.art.vrmcheck.service.Impl;

import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrm.model.model.VRMOfXML;
import com.nuaa.art.vrmcheck.common.utils.OutputUtils;
import com.nuaa.art.vrmcheck.model.*;
import com.nuaa.art.vrmcheck.service.EventHandler;
import com.nuaa.art.vrmcheck.service.ModelCheckModeTransHandler;
import jakarta.annotation.Resource;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

import static com.nuaa.art.vrmcheck.common.utils.OutputUtils.getVariableSetHeader;

@Service
public class ModelCheckModeTransOfXML implements ModelCheckModeTransHandler {
    @Resource
    EventHandler eventHandler;

    @Override
    public boolean checkModeTrans(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        // 检查模式转换表的事件一致性
        Iterator stateMachineIterator = vrmModel.stateMachinesNode.elementIterator();
        while (stateMachineIterator.hasNext()) {// 遍历所有模式集
            ArrayList<String> modesInTable = new ArrayList<String>();
            Element stateMachine = (Element) stateMachineIterator.next();

            Element stateTransition = stateMachine.element("stateTransition");

            Iterator rowIterator = stateTransition.elementIterator();
            while (rowIterator.hasNext()) {// 获取表格中不同的源模式
                String mode = ((Element) rowIterator.next()).element("source").getText();
                if (!modesInTable.contains(mode))
                    modesInTable.add(mode);
            }
            Iterator<String> modesInTableIterator = modesInTable.iterator();
            //System.out.println(modesInTable.toString());
            while (modesInTableIterator.hasNext()) {// 对同源模式的转换进行一致性判断

                String modeInTable = modesInTableIterator.next();// 获取下一个出现在表中的不同源模式
                ArrayList<String> eventForEachRow = new ArrayList<String>();// 每行的事件集合
                ArrayList<String> behaviorForEachRow = new ArrayList<String>();// 每行的目标模式

                rowIterator = stateTransition.elementIterator();
                // 解析每一行事件，包括解析变量与取值、保存每一行源模式、目标模式、事件、事件类型、两个条件
                while (rowIterator.hasNext()) {
                    Element row = (Element) rowIterator.next();
                    String mode = row.element("source").getText();
                    if (mode.equals(modeInTable)) {// 取出源模式为modeInTable的行信息
                        String behavior = row.element("destination").getText();// 该行目标模式
                        behaviorForEachRow.add(behavior);// 该行目标模式存入上层集合
                        eventForEachRow.add(row.element("event").getText().replace(" ", ""));// 该行事件存入上层集合
                    }
                }

                Event ep = new Event(vrmModel, eventForEachRow, behaviorForEachRow);
                ep.parseEventIntoStates();

                String variableSet = OutputUtils.getVariableSetHeader(ep.continualVariables, ep.discreteVariables);

                ArrayList<EventConsistencyError> eces = eventHandler.findConsistencyError(ep);
//                for (EventConsistencyError ece : eces) {
//                    System.out.println("output:" + ece.assignment[0] + "," + ece.assignment[1]);
//                    System.out.println("variables:" + ep.continualVariables.toString()
//                            + ep.discreteVariables.toString());
//                    System.out.println("pre:");
//                    for (ConcreteScenario cs : ece.obeyScenarios[0]) {
//                        for (String value : cs.concreteScenario) {
//                            System.out.print(value + "");
//                        }
//                        System.out.println();
//                    }
//                    System.out.println("post:");
//                    for (ConcreteScenario cs : ece.obeyScenarios[1]) {
//                        for (String value : cs.concreteScenario) {
//                            System.out.print(value + "");
//                        }
//                        System.out.println();
//                    }
//                }

                // 输出出错信息
                if (!eces.isEmpty()) {
                    String outputString = "";// 输出文本
                    for (EventConsistencyError ece : eces) {// 遍历每一组冲突
                        errorReporter.addErrorCount();
                        errorReporter.setModeConvertRight(false);
                        outputString = "错误定位：模式集" + stateMachine.attributeValue("name")
                                + "\n错误内容：当变量取值从下列两表中前者任意一行的组合变换为后者任意一行的组合时，\n会同时从源模式"
                                + modeInTable + "转换到不同的目标模式" + ece.assignment[0] + "和"
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
                        outputString = outputString.substring(0, outputString.length() - 1);
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.EventConsistencyModeTrans,
                                stateMachine.attributeValue("name"),
                                outputString));
                    }
                }
            }
        }
        return errorReporter.isModeConvertRight();
    }
}
