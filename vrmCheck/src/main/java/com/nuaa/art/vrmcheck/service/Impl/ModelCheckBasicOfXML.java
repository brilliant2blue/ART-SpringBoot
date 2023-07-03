package com.nuaa.art.vrmcheck.service.impl;

import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.model.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrm.model.model.VRMOfXML;
import com.nuaa.art.vrmcheck.service.ModelCheckBasicHandler;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

@Service
public class ModelCheckBasicOfXML implements ModelCheckBasicHandler {
    @Override
    public boolean checkInputBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator inputIterator = vrmModel.inputsNode.elementIterator();
        while (inputIterator.hasNext()) {
            Element input = (Element) inputIterator.next();
            String[] wrongLocationName = {"数据类型", "值域", "初始值"};
            boolean[] wrongLocation = {true, true, true, true};
            if (input.elementText("inputType").equals("")) {
                wrongLocation[1] = false;
                wrongLocation[0] = false;
            }
            if (input.elementText("range").equals("")) {
                wrongLocation[2] = false;
                wrongLocation[0] = false;
            }
            if (input.elementText("initialValue").equals("")) {
                wrongLocation[3] = false;
                wrongLocation[0] = false;
            }
            if (!wrongLocation[0]) {
                BasicErrorRefresh(errorReporter);

                String outputString = "错误定位：输入变量" + input.attributeValue("name") + "\n错误内容：该变量的";
                for (int i = 1; i < 4; i++) {
                    if (!wrongLocation[i])
                        outputString += wrongLocationName[i - 1] + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "定义为空";

                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicInputMiss, input.attributeValue("name"), outputString));

            } else {
                String dataType = input.elementText("inputType");
                Iterator typeIterator = vrmModel.typesNode.elementIterator();
                boolean isTypeExist = false;
                while (typeIterator.hasNext()) {
                    Element type = (Element) typeIterator.next();
                    if (type.attributeValue("name").equals(dataType))
                        isTypeExist = true;
                }
                if (!isTypeExist) {
                    BasicErrorRefresh(errorReporter);
                    String outputString = "错误定位：输入变量" + input.attributeValue("name") + "\n错误内容：该变量的数据类型"
                            + input.elementText("inputType") + "不存在";
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicInputType, input.attributeValue("name"), outputString));
                } else {
                    String value = input.elementText("initialValue");
                    boolean isValueViolateType = isValueNotComplyWithType(vrmModel, value, dataType);
                    if (isValueViolateType) {
                        BasicErrorRefresh(errorReporter);

                        String outputString = "错误定位：输入变量" + input.attributeValue("name") + "\n错误内容：该变量的初始值不符合数据类型"
                                + input.elementText("inputType");
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicInputInit, input.attributeValue("name"), outputString));
                    }
                }
            }
        }
        return errorReporter.isBasicRight();
    }

    @Override
    public boolean checkTypeBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {

        Iterator typeIterator = vrmModel.typesNode.elementIterator();
        String[] basicTypes = {"Boolean", "Character", "Double", "Enumerated", "Float", "Integer", "String",
                "Unsigned"};// 所有基本类型
        while (typeIterator.hasNext()) {
            Element type = (Element) typeIterator.next();
            boolean isBasicTypeExist = false;
            String baseType = type.elementText("baseType");
            for (String basicType : basicTypes) {
                if (baseType.equalsIgnoreCase(basicType)) {
                    isBasicTypeExist = true;
                    break;
                }
            }
            if (!isBasicTypeExist) {
                BasicErrorRefresh(errorReporter);
                String outputString = "错误定位：类型" + type.attributeValue("name") + "\n错误内容：该自定义类型所属的基本类型" + baseType
                        + "不属于已知基本类型";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicType, type.attributeValue("name"), outputString));
            }
        }
        return errorReporter.isBasicRight();
    }

    @Override
    public boolean checkConstantBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {

        Iterator constantIterator = vrmModel.constantsNode.elementIterator();
        while (constantIterator.hasNext()) {
            Element constant = (Element) constantIterator.next();
            boolean isValueUndefined = constant.elementText("value").equals("");
            if (isValueUndefined) {
                BasicErrorRefresh(errorReporter);
                String outputString = "错误定位：常量" + constant.attributeValue("name") + "\n错误内容：该常量的值定义为空";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicConstantMiss, constant.attributeValue("name"), outputString));
            } else {
                String dataType = constant.elementText("constantType");
                Iterator typeIterator = vrmModel.typesNode.elementIterator();
                boolean isTypeExist = false;
                while (typeIterator.hasNext()) {
                    Element type = (Element) typeIterator.next();
                    if (type.attributeValue("name").equals(dataType))
                        isTypeExist = true;
                }
                if (!isTypeExist) {
                    BasicErrorRefresh(errorReporter);
                    String outputString = "错误定位：常量" + constant.attributeValue("name") + "\n错误内容：该常量的数据类型"
                            + constant.elementText("constantType") + "不存在";
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicConstantType, constant.attributeValue("name"), outputString));
                } else {
                    String value = constant.elementText("value");
                    boolean isValueViolateType = isValueNotComplyWithType(vrmModel, value, dataType);
                    if (isValueViolateType) {
                        BasicErrorRefresh(errorReporter);
                        String outputString = "错误定位：常量" + constant.attributeValue("name") + "\n错误内容：该常量的值不符合数据类型"
                                + constant.elementText("constantType");
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicConstantValue, constant.attributeValue("name"), outputString));
                    }
                }
            }
        }
        return errorReporter.isBasicRight();
    }

    @Override
    public boolean checkVariableBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator tableIterator = vrmModel.tablesNode.elementIterator();
        while (tableIterator.hasNext()) {
            Element table = (Element) tableIterator.next();
            String[] wrongLocationName = {"数据类型", "值域", "初始值"};
            boolean[] wrongLocation = {true, true, true, true};
            if (table.elementText("tableVariableType").equals("")) {
                wrongLocation[1] = false;
                wrongLocation[0] = false;
            }
            if (table.elementText("range").equals("")) {
                wrongLocation[2] = false;
                wrongLocation[0] = false;
            }
            if (table.elementText("initialValue").equals("")) {
                wrongLocation[3] = false;
                wrongLocation[0] = false;
            }
            if (!wrongLocation[0]) {
                BasicErrorRefresh(errorReporter);
                String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：该变量的";
                for (int i = 1; i < 4; i++) {
                    if (!wrongLocation[i])
                        outputString += wrongLocationName[i - 1] + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "定义为空";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicVariableMiss, table.attributeValue("name"), outputString));
            } else {
                String dataType = table.elementText("tableVariableType");
                Iterator typeIterator_T = vrmModel.typesNode.elementIterator();
                boolean isTypeExist = false;
                while (typeIterator_T.hasNext()) {
                    Element type_T = (Element) typeIterator_T.next();
                    if (type_T.attributeValue("name").equals(dataType))
                        isTypeExist = true;
                }
                if (!isTypeExist) {
                    BasicErrorRefresh(errorReporter);
                    String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：该变量的数据类型"
                            + table.elementText("tableVariableType") + "不存在";
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicVariableType, table.attributeValue("name"), outputString));
                } else {
                    String value = table.elementText("initialValue");
                    boolean isValueViolateType = isValueNotComplyWithType(vrmModel, value, dataType);
                    if (isValueViolateType) {
                        BasicErrorRefresh(errorReporter);
                        String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：该变量的初始值不符合数据类型"
                                + table.elementText("tableVariableType");
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicVariableInit, table.attributeValue("name"), outputString));
                    }
                }
            }
        }
        return errorReporter.isBasicRight();
    }

    @Override
    public boolean checkModeClassBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {
        Iterator stateMachineIterator = vrmModel.stateMachinesNode.elementIterator();
        while (stateMachineIterator.hasNext()) {
            Element stateMachine = (Element) stateMachineIterator.next();
            Element stateList = stateMachine.element("stateList");
            Element stateTransition = stateMachine.element("stateTransition");
            boolean isInitialDefined = false;
            boolean isModeUndefined = false;
            int initialCount = 0;
            int stateIndex = 0;
            ArrayList<Integer> incompleteIndexes = new ArrayList<Integer>();
            ArrayList<String> initialStates = new ArrayList<String>();
            Iterator stateIterator = stateList.elementIterator();
            while (stateIterator.hasNext()) {
                stateIndex++;
                Element state = (Element) stateIterator.next();
                if (state.attribute("initial") != null && (state.attributeValue("initial").equalsIgnoreCase("true")
                        || state.attributeValue("initial").equalsIgnoreCase("1"))) {
                    initialCount++;
                    isInitialDefined = true;
                    if (!initialStates.contains(state.attributeValue("name")))
                        initialStates.add(state.attributeValue("name"));
                }
                if (state.attributeValue("name").equals("") || state.attributeValue("enum").equals("")) {
                    isModeUndefined = true;
                    if (!incompleteIndexes.contains(Integer.valueOf(stateIndex))) {
                        incompleteIndexes.add(stateIndex);
                    }
                }
            }
            if (isModeUndefined) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，模式定义不完整";
                String outputString = "错误定位：模式集" + stateMachine.attributeValue("name") + "\n错误内容：模式表第";
                for (Integer incompleteIndex : incompleteIndexes) {
                    outputString += incompleteIndex + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "行的模式定义不完整";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassMiss, stateMachine.attributeValue("name"), outputString));

            } else if (!isInitialDefined) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，无初始模式";
                String outputString = "错误定位：模式集" + stateMachine.attributeValue("name") + "\n错误内容：模式表不存在初始模式定义";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassNoInit, stateMachine.attributeValue("name"), outputString));
            } else if (initialCount > 1) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，初始模式不唯一";
                String outputString = "错误定位：模式集" + stateMachine.attributeValue("name") + "\n错误内容：定义了多个初始模式";
                for (String initialState : initialStates) {
                    outputString += initialState + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassManyInit, stateMachine.attributeValue("name"), outputString));

            } else {
                if (stateTransition.elements("row").size() == 0) {
                    BasicErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，表格为空";
                    String outputString = "错误定位：模式集" + stateMachine.attributeValue("name") + "\n错误内容：模式转换表为空";
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassTransEmpty, stateMachine.attributeValue("name"), outputString));

                } else {
                    ArrayList<String> modesInTable = new ArrayList<String>();
                    ArrayList<String> modesInClass = new ArrayList<String>();
                    ArrayList<String> notExistMode = new ArrayList<String>();
                    Iterator stateIterator_T = stateList.elementIterator();
                    while (stateIterator_T.hasNext()) {
                        Element state = (Element) stateIterator_T.next();
                        modesInClass.add(state.attributeValue("name"));
                    }
                    Iterator rowIterator = stateTransition.elementIterator();
                    while (rowIterator.hasNext()) {
                        Element row = (Element) rowIterator.next();
                        String source = row.elementText("source");
                        String destinate = row.elementText("destination");
                        if (!modesInTable.contains(source))
                            modesInTable.add(source);
                        if (!modesInTable.contains(destinate))
                            modesInTable.add(destinate);
                    }
                    boolean isModeNotExist = false;
                    for (String modeInTable : modesInTable) {
                        boolean isModeExist = false;
                        for (String modeInClass : modesInClass) {
                            if (modeInTable.equals(modeInClass)) {
                                isModeExist = true;
                                break;
                            }
                        }
                        if (!isModeExist) {
                            isModeNotExist = true;
                            if (!notExistMode.contains(modeInTable))
                                notExistMode.add(modeInTable);
                        }
                    }
                    if (isModeNotExist) {
                        BasicErrorRefresh(errorReporter);
                        //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，模式不存在";
                        String outputString = "错误定位：模式集" + stateMachine.attributeValue("name")
                                + "\n错误内容：模式转换表涉及了模式表中不存在的模式";
                        for (String modeName : notExistMode) {
                            outputString += modeName + "、";
                        }
                        outputString = outputString.substring(0, outputString.length() - 1);
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassModeMiss, stateMachine.attributeValue("name"), outputString));

                    } else {
                        boolean isEventWrong = false;
                        boolean isConditionWrong = false;
                        ArrayList<Integer> eventWrongRowNumbers = new ArrayList<Integer>();
                        ArrayList<Integer> conditionWrongRowNumbers = new ArrayList<Integer>();
                        ArrayList<String> variables = new ArrayList<String>();
                        ArrayList<String> values = new ArrayList<String>();
                        int currentRowIndex = 0;
                        rowIterator = stateTransition.elementIterator();
                        while (rowIterator.hasNext()) {
                            currentRowIndex++;
                            Element row = (Element) rowIterator.next();
                            String wholeEvent = row.element("event").getText().replace(" ", "");
                            int flag = isEventOrConditionWrong(variables, values, wholeEvent);
                            if (flag == 1) {
                                isEventWrong = true;
                                if (!eventWrongRowNumbers.contains(Integer.valueOf(currentRowIndex)))
                                    eventWrongRowNumbers.add(currentRowIndex);
                            }
                            if (flag == 2) {
                                isConditionWrong = true;
                                if (!conditionWrongRowNumbers.contains(Integer.valueOf(currentRowIndex)))
                                    conditionWrongRowNumbers.add(currentRowIndex);
                            }
                        }
                        if (isEventWrong) {
                            BasicErrorRefresh(errorReporter);
                            //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，事件语法错误";
                            String outputString = "错误定位：模式集" + stateMachine.attributeValue("name") + "\n错误内容：模式转换表第";
                            for (Integer number : eventWrongRowNumbers) {
                                outputString += number.intValue() + "、";
                            }
                            outputString = outputString.substring(0, outputString.length() - 1);
                            outputString += "行的事件语法有误";
                            errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassEvent, stateMachine.attributeValue("name"), outputString));

                        } else if (isConditionWrong) {
                            BasicErrorRefresh(errorReporter);
                            //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，条件语法错误";
                            String outputString = "错误定位：模式集" + stateMachine.attributeValue("name") + "\n错误内容：模式转换表第";
                            for (Integer number : conditionWrongRowNumbers) {
                                outputString += number.intValue() + "、";
                            }
                            outputString = outputString.substring(0, outputString.length() - 1);
                            outputString += "行事件中的条件语法有误";
                            errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassCondition, stateMachine.attributeValue("name"), outputString));

                        } else {
                            boolean isVariableUndefined = false;
                            boolean isValueViolateType = false;
                            ArrayList<String> undefinedVariables = new ArrayList<String>();
                            ArrayList<String> violateVariables = new ArrayList<String>();
                            for (int j = 0; j < variables.size(); j++) {
                                String vvariable = variables.get(j);
                                String value_T = values.get(variables.indexOf(vvariable));
                                int flag = isVariableUndefinedOrViolateType(vrmModel, vvariable, value_T);
                                if (flag == 1) {
                                    isVariableUndefined = true;
                                    if (!undefinedVariables.contains(vvariable))
                                        undefinedVariables.add(vvariable);
                                }
                                if (flag == 2) {
                                    isValueViolateType = true;
                                    if (!violateVariables.contains(vvariable))
                                        violateVariables.add(vvariable);
                                }
                            }
                            if (isVariableUndefined) {
                                BasicErrorRefresh(errorReporter);
                                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量不存在";
                                String outputString = "错误定位：模式集" + stateMachine.attributeValue("name")
                                        + "\n错误内容：模式转换表中的变量";
                                for (String undefinedVariable : undefinedVariables) {
                                    outputString += undefinedVariable + "、";
                                }
                                outputString = outputString.substring(0, outputString.length() - 1);
                                outputString += "未在输入变量、中间或输出变量、模式集中定义";
                                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassVariableMiss, stateMachine.attributeValue("name"), outputString));

                            } else if (isValueViolateType) {
                                BasicErrorRefresh(errorReporter);
                                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量的值不符合数据类型";
                                String outputString = "错误定位：模式集" + stateMachine.attributeValue("name")
                                        + "\n错误内容：模式转换表中的变量";
                                for (String violateVariable : violateVariables) {
                                    outputString += violateVariable + "、";
                                }
                                outputString = outputString.substring(0, outputString.length() - 1);
                                outputString += "其逻辑表达式右侧值不符合变量的数据类型";
                                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassVariableType, stateMachine.attributeValue("name"), outputString));
                            }
                        }
                    }
                }
            }
        }
        return errorReporter.isBasicRight();
    }

    @Override
    public boolean checkTableBasic(VRMOfXML vrmModel, CheckErrorReporter errorReporter) {

        Iterator tableIterator = vrmModel.tablesNode.elementIterator();
        while (tableIterator.hasNext()) {
            Element table = (Element) tableIterator.next();
            if (table.element("eventTable") != null) {// 事件表
                boolean isEventWrong = false;
                boolean isConditionWrong = false;
                ArrayList<Integer> eventWrongRowNumbers = new ArrayList<Integer>();
                ArrayList<Integer> conditionWrongRowNumbers = new ArrayList<Integer>();
                ArrayList<Integer> eventWrongRowReId = new ArrayList<Integer>();
                ArrayList<Integer> conditionWrongRowReId = new ArrayList<Integer>();
                ArrayList<String> variables = new ArrayList<String>();
                ArrayList<String> values = new ArrayList<String>();
                int currentRowIndex = 0;
                Element eventTable = table.element("eventTable");
                Iterator rowIterator = eventTable.elementIterator();

                // 检查事件表中 语法的错误
                while (rowIterator.hasNext()) {
                    currentRowIndex++;
                    Element row = (Element) rowIterator.next();
                    String wholeEvent = row.element("event").getText().replace(" ", "");
                    int flag = isEventOrConditionWrong(variables, values, wholeEvent); //此步会同时生成variables列表
                    System.out.println(variables);
                    if (flag == 1) {
                        isEventWrong = true;
                        if (!eventWrongRowNumbers.contains(Integer.valueOf(currentRowIndex))) {
                            eventWrongRowNumbers.add(currentRowIndex);
                            eventWrongRowReId.add(Integer.valueOf(row.elementText("relateSReq")));
                        }
                    }
                    if (flag == 2) {
                        isConditionWrong = true;
                        if (!conditionWrongRowNumbers.contains(Integer.valueOf(currentRowIndex))) {
                            conditionWrongRowNumbers.add(currentRowIndex);
                            conditionWrongRowReId.add(Integer.valueOf(row.elementText("relateSReq")));
                        }
                    }
                }
                if (isEventWrong) {
                    BasicErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，事件语法错误";
                    String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：事件表第";
                    for (Integer number : eventWrongRowNumbers) {
                        outputString += number.intValue() + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "行的事件语法有误";
                    errorReporter.addErrorList(new CheckErrorInfo(
                            errorReporter.getErrorCount(),
                            CheckErrorType.BasicEvent,
                            table.attributeValue("name"),
                            eventWrongRowReId, outputString));
                } else if (isConditionWrong) {
                    BasicErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，条件语法错误";
                    String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：事件表第";
                    for (Integer number : conditionWrongRowNumbers) {
                        outputString += number.intValue() + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "行事件中的条件语法有误";
                    errorReporter.addErrorList(new CheckErrorInfo(
                            errorReporter.getErrorCount(),
                            CheckErrorType.BasicCondition,
                            table.attributeValue("name"),
                            conditionWrongRowReId, outputString));
                }
                // 检查事件表中 元素引用的错误
                else {
                    boolean isVariableUndefined = false;
                    boolean isValueViolateType_T = false;
                    ArrayList<String> undefinedVariables = new ArrayList<String>();
                    ArrayList<String> violateVariables = new ArrayList<String>();
                    for (int j = 0; j < variables.size(); j++) {
                        String vvariable = variables.get(j);
                        String value_T = values.get(variables.indexOf(vvariable));
                        int flag = isVariableUndefinedOrViolateType(vrmModel, vvariable, value_T);
                        if (flag == 1) {
                            isVariableUndefined = true;
                            if (!undefinedVariables.contains(vvariable))
                                undefinedVariables.add(vvariable);
                        }
                        if (flag == 2) {
                            isValueViolateType_T = true;
                            if (!violateVariables.contains(vvariable))
                                violateVariables.add(vvariable);
                        }
                    }
                    if (isVariableUndefined) {
                        BasicErrorRefresh(errorReporter);
                        //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量不存在";
                        String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：事件表中的变量";
                        for (String undefinedVariable : undefinedVariables) {
                            outputString += undefinedVariable + "、";
                        }
                        outputString = outputString.substring(0, outputString.length() - 1);
                        outputString += "未在输入变量、中间或输出变量、模式集中定义";
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.BasicEventVariableMiss,
                                table.attributeValue("name"),
                                outputString));
                    } else if (isValueViolateType_T) {
                        BasicErrorRefresh(errorReporter);
                        //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量的值不符合数据类型";
                        String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：事件表中的变量";
                        for (String violateVariable : violateVariables) {
                            outputString += violateVariable + "、";
                        }
                        outputString = outputString.substring(0, outputString.length() - 1);
                        outputString += "其逻辑表达式右侧值不符合变量的数据类型";
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.BasicEventVariableType,
                                table.attributeValue("name"),
                                outputString));
                    }
                }
            } else if (table.element("conditionTable") != null) {
                boolean isConditionWrong = false;
                ArrayList<Integer> conditionWrongRowNumbers = new ArrayList<Integer>();
                ArrayList<Integer> conditionWrongRowReqId = new ArrayList<Integer>();
                ArrayList<String> variables = new ArrayList<String>();
                ArrayList<String> values = new ArrayList<String>();
                int currentRowIndex = 0;
                Element conditionTable = table.element("conditionTable");
                Iterator rowIterator = conditionTable.elementIterator();
                while (rowIterator.hasNext()) {
                    currentRowIndex++;
                    Element row = (Element) rowIterator.next();
                    String condition = row.elementText("condition").replace(" ", "");
                    if (isConditionWrong(variables, values, condition)) {
                        isConditionWrong = true;
                        if (!conditionWrongRowNumbers.contains(Integer.valueOf(currentRowIndex))) {
                            conditionWrongRowNumbers.add(currentRowIndex);
                            conditionWrongRowReqId.add(Integer.valueOf(row.elementText("relateSReq")));
                        }
                    }
                }
                if (isConditionWrong) {
                    BasicErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，条件语法错误";
                    String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：条件表第";
                    for (Integer number : conditionWrongRowNumbers) {
                        outputString += number.intValue() + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "行条件语法有误";
                    errorReporter.addErrorList(new CheckErrorInfo(
                            errorReporter.getErrorCount(),
                            CheckErrorType.BasicCondition,
                            table.attributeValue("name"),
                            conditionWrongRowReqId,
                            outputString));
                } else {
                    boolean isVariableUndefined = false;
                    boolean isValueViolateType_T = false;
                    ArrayList<String> undefinedVariables = new ArrayList<String>();
                    ArrayList<String> violateVariables = new ArrayList<String>();
                    for (int j = 0; j < variables.size(); j++) {
                        String vvariable = variables.get(j);
                        String value_T = values.get(variables.indexOf(vvariable));
                        int flag = isVariableUndefinedOrViolateType(vrmModel, vvariable, value_T);
                        if (flag == 1) {
                            isVariableUndefined = true;
                            if (!undefinedVariables.contains(vvariable))
                                undefinedVariables.add(vvariable);
                        }
                        if (flag == 2) {
                            isValueViolateType_T = true;
                            if (!violateVariables.contains(vvariable))
                                violateVariables.add(vvariable);
                        }
                    }
                    if (isVariableUndefined) {
                        BasicErrorRefresh(errorReporter);
                        String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：条件表中的变量";
                        for (String undefinedVariable : undefinedVariables) {
                            outputString += undefinedVariable + "、";
                        }
                        outputString = outputString.substring(0, outputString.length() - 1);
                        outputString += "未在输入变量、中间或输出变量、模式集中定义";
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.BasicConditionVariableMiss,
                                table.attributeValue("name"),
                                outputString));
                    } else if (isValueViolateType_T) {
                        BasicErrorRefresh(errorReporter);
                        //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量的值不符合数据类型";
                        String outputString = "错误定位：表格" + table.attributeValue("name") + "\n错误内容：条件表中的变量";
                        for (String violateVariable : violateVariables) {
                            outputString += violateVariable + "、";
                        }
                        outputString = outputString.substring(0, outputString.length() - 1);
                        outputString += "其逻辑表达式右侧值不符合变量的数据类型";
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.BasicConditionVariableType,
                                table.attributeValue("name"),
                                outputString));
                    }
                }
            }
        }
        return errorReporter.isBasicRight();
    }

    /**
     * 检查值与类型是否匹配，用于基础语法规范检查
     */
    boolean isValueNotComplyWithType(VRMOfXML vrmModel, String value, String dataType) {
        boolean isNotComply = false;
        Element thisType = null;
        Iterator typeIterator = vrmModel.typesNode.elementIterator();
        while (typeIterator.hasNext()) {
            Element type = (Element) typeIterator.next();
            if (type.attributeValue("name").equals(dataType))
                thisType = type;
        }
        String range = "";
        try {
            range = thisType.elementText("range");
        } catch (NullPointerException e) {
            //log.info(dataType);
            System.out.println(dataType);
        }
        String baseType = thisType.elementText("baseType");
        if (baseType.equalsIgnoreCase("String")) {

        } else if (baseType.equalsIgnoreCase("Enumerated")) {
            boolean isValueExist = false;
            String[] ranges = range.split(",");
            for (String singleRange : ranges) {
                if (singleRange.contains("=")) {
                    singleRange = singleRange.substring(0, singleRange.indexOf("="));
                }
                if (singleRange.equals(value)) {
                    isValueExist = true;
                    break;
                }
            }
            if (!isValueExist)
                isNotComply = true;
        } else if (range.contains(",")) {
            boolean isValueExist = false;
            String[] ranges = range.split(",");
            for (String singleRange : ranges) {
                if (singleRange.contains("=")) {
                    singleRange = singleRange.substring(0, singleRange.indexOf("="));
                }
                if (singleRange.equals(value)) {
                    isValueExist = true;
                    break;
                }
            }
            if (!isValueExist)
                isNotComply = true;
        } else {
            try {
                if (range.contains("\\")) {
                    String left = range.substring(1, range.indexOf(".."));
                    String right = range.substring(range.indexOf("..") + 3);
                    if (value.length() != 1)
                        isNotComply = true;
                    else {
                        int asciiOfChar = Integer.valueOf(value);
                        int leftValue = Integer.parseInt(left);
                        int rightValue = Integer.parseInt(right);
                        if (asciiOfChar < leftValue || asciiOfChar > rightValue)
                            isNotComply = true;
                    }
                } else if (range.contains("e")) {
                    String left = range.substring(0, range.indexOf(".."));
                    String right = range.substring(range.indexOf("..") + 2);
                    double leftValue = Double.parseDouble(left.substring(0, left.indexOf("e")))
                            * Math.pow(10, Double.parseDouble(left.substring(left.indexOf("e") + 1)));
                    double rightValue = Double.parseDouble(right.substring(0, right.indexOf("e")))
                            * Math.pow(10, Double.parseDouble(right.substring(right.indexOf("e") + 1)));
                    double vValue = Float.parseFloat(value);
                    if (vValue < leftValue || vValue > rightValue)
                        isNotComply = true;
                } else if (range.contains("E")) {
                    String left = range.substring(0, range.indexOf(".."));
                    String right = range.substring(range.indexOf("..") + 2);
                    double leftValue = Double.parseDouble(left.substring(0, left.indexOf("E")))
                            * Math.pow(10, Double.parseDouble(left.substring(left.indexOf("E") + 1)));
                    double rightValue = Double.parseDouble(right.substring(0, right.indexOf("E")))
                            * Math.pow(10, Double.parseDouble(right.substring(right.indexOf("E") + 1)));
                    double vValue = Float.parseFloat(value);
                    if (vValue < leftValue || vValue > rightValue)
                        isNotComply = true;
                } else {
                    String left = range.substring(0, range.indexOf(".."));
                    String right = range.substring(range.indexOf("..") + 2);
                    float leftValue = Float.parseFloat(left);
                    float rightValue = Float.parseFloat(right);
                    float vValue = Float.parseFloat(value);
                    if (vValue > rightValue || vValue < leftValue) {
                        isNotComply = true;
                    }
                }
            } catch (Exception ex) {
                isNotComply = true;
            }
        }
        return isNotComply;
    }

    /**
     * 检查变量是否定义或者取值是否合法，用于基础语法检查
     *
     * @return 1 变量不存在
     * @return 2 变量类型错误
     */
    int isVariableUndefinedOrViolateType(VRMOfXML vrmModel, String vvariable, String value_T) {
        Iterator inputIterator_T = vrmModel.inputsNode.elementIterator();
        Iterator tableIterator_T = vrmModel.tablesNode.elementIterator();
        Iterator stateMachineIterator_T = vrmModel.stateMachinesNode.elementIterator();
        boolean isVariableExist = false;
        boolean isCurrentValueViolateType = false;
        while (inputIterator_T.hasNext()) {// 判断变量名是否为输入变量
            Element input_T = (Element) inputIterator_T.next();
            String name = input_T.attributeValue("name");
            if (vvariable.equals(name)) {// 变量名是输入变量
                isVariableExist = true;
                String type_T = input_T.element("inputType").getText();
                if (isValueNotComplyWithType(vrmModel, value_T, type_T)) {
                    isCurrentValueViolateType = true;
                }
            }
        }
        while (tableIterator_T.hasNext()) {// 判断是否为中间、输出变量
            Element table_T = (Element) tableIterator_T.next();
            String name = table_T.attributeValue("name");
            if (vvariable.equals(name)) {// 变量名是中间、输出变量
                isVariableExist = true;
                String type_T = table_T.element("tableVariableType").getText();
                if (isValueNotComplyWithType(vrmModel, value_T, type_T)) {
                    isCurrentValueViolateType = true;
                }
            }
        }
        while (stateMachineIterator_T.hasNext()) {// 判断是否为模式集
            Element stateMachine_T = (Element) stateMachineIterator_T.next();
            Element stateListNode = stateMachine_T.element("stateList");
            String name = stateMachine_T.attributeValue("name");
            if (vvariable.equals(name)) {
                isVariableExist = true;
                Iterator statesIterator = stateListNode.elementIterator();
                boolean isValueExist = false;
                while (statesIterator.hasNext()) {
                    Element state = (Element) statesIterator.next();
                    if (value_T.equals(state.attributeValue("name")))
                        isValueExist = true;
                }
                if (!isValueExist)
                    isCurrentValueViolateType = true;
            }
        }
        if (!isVariableExist)
            return 1;
        else if (isCurrentValueViolateType)
            return 2;
        else
            return 0;
    }

    /**
     * 检查条件是否符合语法规范
     *
     * @return boolean
     */
    boolean isConditionWrong(ArrayList<String> variables, ArrayList<String> values, String condition) {
        boolean isConditionWrong = false;
        String[] orConditions = condition.split("\\|\\|");
        for (int i = 0; i < orConditions.length; i++) {
            String orCondition = orConditions[i];
            if (orCondition.equals("()") || orCondition.equals(""))
                orCondition = "";
            else {
                if (orCondition.indexOf('(') == 0)
                    orCondition = orCondition.substring(1);
                if (orCondition.lastIndexOf(')') == orCondition.length() - 1)
                    orCondition = orCondition.substring(0, orCondition.length() - 1);
            }
            String[] andConditions = orCondition.split("&");
            for (String andCondition : andConditions) {
                String content = andCondition;
                if (andCondition.equals("()") || andCondition.equals(""))
                    content = "";
                else {
//                    if (andCondition.indexOf('(') == 0)
//                        content = content.substring(1);
//                    if (andCondition.lastIndexOf(')') == andCondition.length() - 1)
//                        content = content.substring(0, content.length() - 1);
                    content = content.replace("(","").replace(")","");
                }
                if (content.split("<=").length != 2 && content.split(">=").length != 2 && content.split("=").length != 2
                        && content.split("<").length != 2 && content.split(">").length != 2
                        && !content.equalsIgnoreCase("true") && !content.equalsIgnoreCase("false")) {
                    isConditionWrong = true;
                    continue;
                }
                String tag = "";
                if (andCondition.contains("<="))
                    tag = "<=";
                else if (andCondition.contains(">="))
                    tag = ">=";
                else if (andCondition.contains("!="))
                    tag = "!=";
                else if (andCondition.contains("="))
                    tag = "=";
                else if (andCondition.contains("<"))
                    tag = "<";
                else if (andCondition.contains(">"))
                    tag = ">";
                else if (andCondition.equalsIgnoreCase("true") || andCondition.equalsIgnoreCase("false"))
                    tag = "TrueOrFalse";
                if (tag != "TrueOrFalse" && tag != "") {
                    String variable = content.substring(0, content.indexOf(tag));
                    String vvalue;
                    if (tag == ">=" || tag == "<=" || tag == "!=")
                        vvalue = content.substring(content.indexOf(tag) + 2);
                    else
                        vvalue = content.substring(content.indexOf(tag) + 1);
                    if (variable.charAt(0) == '!')
                        variable = variable.substring(1);
                    variables.add(variable);
                    values.add(vvalue);
                }
            }
        }
        return isConditionWrong;
    }


    /**
     * 检查事件语法定义是否符合基本语法要求
     *
     * @return 1 事件语法错误
     * @return 2 条件语法错误
     */
    int isEventOrConditionWrong(ArrayList<String> variables, ArrayList<String> values, String wholeEvent) {
        boolean isEventWrong = false;
        boolean isConditionWrong = false;
        String[] andEvents = wholeEvent.split("\\}\\{");// 分离AND事件
        for (int j = 0; j < andEvents.length; j++) {
            String andEvent = andEvents[j];
            if (andEvent.length() == 0) {
                isEventWrong = true;
                continue;
            }
            if (j == 0) {
                if (andEvent.charAt(0) != '{') {
                    isEventWrong = true;
                    continue;
                } else
                    andEvent = andEvent.substring(1);
            }
            if (j == andEvents.length - 1) {
                if (andEvent.charAt(andEvent.length() - 1) != '}') {
                    isEventWrong = true;
                    continue;
                } else
                    andEvent = andEvent.substring(0, andEvent.length() - 1);
            }
            String eevent = andEvent;
            if ((!eevent.equals("never")) && (!eevent.equals("NEVER")) && (!eevent.contains("when"))
                    && (!eevent.contains("WHEN")) && (!eevent.contains("while")) && (!eevent.contains("WHILE"))
                    && (!eevent.contains("where")) && (!eevent.contains("WHERE")))
                eevent = eevent + "when(true)";
            if ((!Pattern.compile("^\\@[TFC]\\(").matcher(eevent).find()
                    || !Pattern.compile("\\)$").matcher(eevent).find()
                    || (eevent.split("\\)WHEN\\(").length != 2 && eevent.split("\\)WHERE\\(").length != 2
                    && eevent.split("\\)WHILE\\(").length != 2 && eevent.split("\\)when\\(").length != 2
                    && eevent.split("\\)where\\(").length != 2 && eevent.split("\\)while\\(").length != 2))
                    && eevent.equals("never") && eevent.equals("NEVER")) {
                isEventWrong = true;
                continue;
            }
            String etag = "";
            if (eevent.contains("when") || eevent.contains("WHEN"))
                etag = "when";
            else if (eevent.contains("where") || eevent.contains("WHERE"))
                etag = "where";
            else if (eevent.contains("while") || eevent.contains("WHILE"))
                etag = "while";
            else if (eevent == "never" || eevent == "NEVER")
                etag = "never";
            if (!etag.equals("never") && !etag.equals("")) {
                String[] conditions;
                if (eevent.contains(etag))
                    conditions = eevent.split(etag);
                else
                    conditions = eevent.split(etag.toUpperCase());
                if (conditions[0].length() - 1 == 3)
                    conditions[0] = "";
                else
                    conditions[0] = conditions[0].substring(3, conditions[0].length() - 1);
                if (conditions[1].length() - 1 == 1)
                    conditions[1] = "";
                else
                    conditions[1] = conditions[1].substring(1, conditions[1].length() - 1);
                for (String condition : conditions) {
                    if (isConditionWrong(variables, values, condition)) {
                        isConditionWrong = true;
                    }
                }
            }
        }
        if (isEventWrong)
            return 1;
        else if (isConditionWrong)
            return 2;
        else
            return 0;
    }

    void BasicErrorRefresh(CheckErrorReporter errorReporter) {
        errorReporter.setBasicRight(false);
        errorReporter.addErrorCount();
    }
}

