package com.nuaa.art.vrmcheck.service.Impl;

import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.model.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.model.VRMOfXML;
import com.nuaa.art.vrmcheck.service.ModelCheckInputHandler;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;

@Service
public class ModelCheckInputOfXML implements ModelCheckInputHandler {
    /**
     * 检查条件表输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkInputIntegrityOfCondition(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator tableIterator = vrmModel.tablesNode.elementIterator();
        while (tableIterator.hasNext()) {
            Element table = (Element) tableIterator.next();
            if (table.element("conditionTable") != null) {// 事件表
                checkInputIntegrity(table, vrmModel.stateMachinesNode, errorReporter,"conditionTable");
            }
        }
        return errorReporter.isInputIntegrityRight();
    }

    /**
     * 检查事件表输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkInputIntegrityOfEvent(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator tableIterator = vrmModel.tablesNode.elementIterator();
        while (tableIterator.hasNext()) {
            Element table = (Element) tableIterator.next();
            if (table.element("eventTable") != null) {// 事件表
                checkInputIntegrity(table, vrmModel.stateMachinesNode, errorReporter,"eventTable");
            }
        }
        return errorReporter.isInputIntegrityRight();
    }

    /**
     * 检查模式转换表的输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkInputIntegrityOfModeTrans(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
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
            boolean isFirstRight = true;
            ArrayList<String> modesInClass = new ArrayList<String>();
            rowIterator = stateTransition.elementIterator();
            Iterator modeIterator = stateMachine.element("stateList").elementIterator();
            while (modeIterator.hasNext()) {
                Element mode = (Element) modeIterator.next();
                if (!modesInClass.contains(mode.attributeValue("name")))
                    modesInClass.add(mode.attributeValue("name"));
            }
            boolean isModeNotExist = false;
            boolean isModeNotAll = false;
            ArrayList<String> modesNotExist = new ArrayList<String>();
            ArrayList<String> modesNotContained = new ArrayList<String>();
            for (String modeInClass : modesInClass) {
                if (!modesInTable.contains(modeInClass)) {
                    modesNotContained.add(modeInClass);
                    isModeNotAll = true;
                }
            }
            for (String modeInTable : modesInTable) {
                if (!modesInClass.contains(modeInTable)) {
                    modesNotExist.add(modeInTable);
                    isModeNotExist = true;
                }
            }
            if (isModeNotExist) {
                InputErrorRefresh(errorReporter);
                String outputString = "错误定位：模式集" + stateMachine.attributeValue("name")
                        + "\n错误内容：该模式集模式转换表中的模式";
                for (String modeNotExist : modesNotExist) {
                    outputString += modeNotExist + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "未在模式集中定义";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.InputIntegrityModeTransModeMiss, stateMachine.attributeValue("name"), outputString));
            }
            if (isModeNotAll) {
                InputErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：第一范式检查，违反输入完整性";
                String outputString = "错误定位：模式集" + stateMachine.attributeValue("name")
                        + "\n错误内容：没有离开该模式集的模式";
                for (String modeNotContained : modesNotContained) {
                    outputString += modeNotContained + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "的模式转换";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.InputIntegrityModeTransNextMiss, stateMachine.attributeValue("name"), outputString));
            }
        }
        return errorReporter.isInputIntegrityRight();
    }

    /**
     * 检查输入完整性
     *
     * @param table             表格
     * @param stateMachinesNode 状态机
     * @param errorReporter     错误报告
     * @param type              表格类型
     * @return boolean
     */
    void checkInputIntegrity(Element table, Element stateMachinesNode,CheckErrorReporter errorReporter, String type){
        String referencedStateMachine;
        if (table.element("referencedStateMachine").attribute("name") != null)
            referencedStateMachine = table.element("referencedStateMachine").attributeValue("name");
        else
            referencedStateMachine = "";
        // 输入完整性
        boolean isFirstRight = true;
        if (referencedStateMachine != null && (!referencedStateMachine.equals(""))) {
            ArrayList<String> modesInTable = new ArrayList<String>();
            ArrayList<String> modesInClass = new ArrayList<String>();
            Element depend = null;
            Iterator stateMachineIterator_T = stateMachinesNode.elementIterator();
            while (stateMachineIterator_T.hasNext()) {// 找到模式依赖
                Element stateMachine_T = (Element) stateMachineIterator_T.next();
                if (stateMachine_T.attributeValue("name").equals(referencedStateMachine))
                    depend = stateMachine_T;
            }
            if (depend == null) {
                InputErrorRefresh(errorReporter);
                String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：该表格依赖的模式集"
                        + referencedStateMachine + "未在模式集字典中定义";
                if(type.equals("eventTable")){
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityEventModeClassMiss,table.attributeValue("name"),outputString));
                } else {
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityConditionModeClassMiss,table.attributeValue("name"),outputString));
                }
            } else {

                Iterator rowIterator= table.element(type).elementIterator();
                while (rowIterator.hasNext()) {
                    Element row = (Element) rowIterator.next();
                    if (!modesInTable.contains(row.elementText("state")))
                        modesInTable.add(row.elementText("state"));
                }
                Iterator modeIterator = depend.element("stateList").elementIterator();
                while (modeIterator.hasNext()) {
                    Element mode = (Element) modeIterator.next();
                    if (!modesInClass.contains(mode.attributeValue("name")))
                        modesInClass.add(mode.attributeValue("name"));
                }
                boolean isModeNotExist = false;
                boolean isModeNotAll = false;
                ArrayList<String> modesNotExist = new ArrayList<String>();
                ArrayList<String> modesNotContained = new ArrayList<String>();
                for (String modeInClass : modesInClass) {
                    if (!modesInTable.contains(modeInClass)) {
                        modesNotContained.add(modeInClass);
                        isModeNotAll = true;
                    }
                }
                for (String modeInTable : modesInTable) {
                    if (!modesInClass.contains(modeInTable)) {
                        modesNotExist.add(modeInTable);
                        isModeNotExist = true;
                    }
                }
                if (isModeNotExist) {
                    InputErrorRefresh(errorReporter);
                    String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：该表格依赖的模式";
                    for (String modeNotExist : modesNotExist) {
                        outputString += modeNotExist + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "未在依赖的模式集中定义";
                    if(type.equals("eventTable")){
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityEventModeMiss,table.attributeValue("name"),outputString));
                    } else {
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityConditionModeMiss,table.attributeValue("name"),outputString));
                    }
                }
                if (isModeNotAll) {
                    InputErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：第一范式检查，违反输入完整性";
                    String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：该表格依赖的模式集的模式";
                    for (String modeNotContained : modesNotContained) {
                        outputString += modeNotContained + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "未在表格中涉及";
                    if(type.equals("eventTable")){
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityEventModeUnmeet,table.attributeValue("name"),outputString));
                    } else {
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityConditionModeUnmeet,table.attributeValue("name"),outputString));
                    }
                }
            }
        }
    }

    void InputErrorRefresh(CheckErrorReporter errorReporter){
        errorReporter.setInputIntegrityRight(false);
        errorReporter.addErrorCount();
    }
}
