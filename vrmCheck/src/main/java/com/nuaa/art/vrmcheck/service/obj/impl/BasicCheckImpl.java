package com.nuaa.art.vrmcheck.service.obj.impl;

import com.nuaa.art.vrm.common.BasicDataType;
import com.nuaa.art.vrm.common.utils.ConditionTableUtils;
import com.nuaa.art.vrm.common.utils.DataTypeUtils;
import com.nuaa.art.vrm.common.utils.EventTableUtils;
import com.nuaa.art.vrm.entity.ConceptLibrary;
import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.entity.Type;
import com.nuaa.art.vrm.model.*;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.model.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.obj.BasicCheck;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 基本语法检查
 *
 * @author konsin
 * @date 2023/09/12
 */
@Service
public class BasicCheckImpl implements BasicCheck {

    @Resource
    EventTableUtils eventTableUtils;

    @Resource
    ConditionTableUtils conditionTableUtils;

    void BasicErrorRefresh(CheckErrorReporter errorReporter) {
        errorReporter.setBasicRight(false);
        errorReporter.addErrorCount();
    }

    /**
     * 检查输入变量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkInputBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
        if (vrmModel.getInputs().isEmpty()) return true;
        for (ConceptLibrary ip : vrmModel.getInputs()) {
            String[] wrongLocationName = {"数据类型", "值域", "初始值"};
            boolean[] wrongLocation = {true, true, true, true};
            if (ip.getConceptDatatype() == null || ip.getConceptDatatype().isBlank()) {
                wrongLocation[1] = false;
                wrongLocation[0] = false;
            }
            if (ip.getConceptRange() == null || ip.getConceptRange().isBlank()) {
                wrongLocation[2] = false;
                wrongLocation[0] = false;
            }
            if (ip.getConceptValue() == null || ip.getConceptValue().isBlank()) {
                wrongLocation[3] = false;
                wrongLocation[0] = false;
            }
            if (!wrongLocation[0]) {
                BasicErrorRefresh(errorReporter);

                String outputString = "错误定位：输入变量" + ip.getConceptName() + "\n错误内容：该变量的";
                for (int i = 1; i < 4; i++) {
                    if (!wrongLocation[i])
                        outputString += wrongLocationName[i - 1] + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "定义为空";

                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicInputMiss, ip.getConceptName(), outputString));

            } else {
                String dataType = ip.getConceptDatatype();
                Type thisType = null;
                for (Type type : vrmModel.getTypes()) {
                    if (type.getTypeName().equals(dataType)) {
                        thisType = type;
                        break;
                    }
                }
                if (thisType == null) {
                    BasicErrorRefresh(errorReporter);
                    String outputString = "错误定位：输入变量" + ip.getConceptName() + "\n错误内容：该变量的数据类型"
                            + ip.getConceptDatatype() + "不存在";
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicInputType, ip.getConceptName(), outputString));
                } else {
                    String value = ip.getConceptValue();
                    boolean isValueViolateType = isValueNotComplyWithType(thisType, value);
                    if (isValueViolateType) {
                        BasicErrorRefresh(errorReporter);

                        String outputString = "错误定位：输入变量" + ip.getConceptName() + "\n错误内容：该变量的初始值不符合数据类型"
                                + ip.getConceptDatatype();
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicInputInit, ip.getConceptName(), outputString));
                    }
                }
            }
        }
        return errorReporter.isBasicRight();
    }

    /**
     * 检查类型定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkTypeBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
        if (vrmModel.getTypes().isEmpty()) return true;
        for (Type type : vrmModel.getTypes()) {
            boolean isBasicTypeExist = false;
            String baseType = type.getDataType();
            if (BasicDataType.findTypeByName(baseType) != null) {
                isBasicTypeExist = true;
            }

            if (!isBasicTypeExist) {
                BasicErrorRefresh(errorReporter);
                String outputString = "错误定位：类型" + type.getTypeName() + "\n错误内容：该自定义类型所属的基本类型" + baseType
                        + "不属于已知基本类型";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicType, type.getTypeName(), outputString));
            }

            // todo 添加类型值域的错误分析？
        }
        return errorReporter.isBasicRight();
    }

    /**
     * 检查常量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkConstantBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
        if (vrmModel.getConstants().isEmpty()) return true;
        for (ConceptLibrary constant : vrmModel.getConstants()) {
            boolean isValueUndefined = constant.getConceptValue().isBlank();
            if (isValueUndefined) {
                BasicErrorRefresh(errorReporter);
                String outputString = "错误定位：常量" + constant.getConceptName() + "\n错误内容：该常量的值定义为空";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicConstantMiss, constant.getConceptName(), outputString));
            } else {
                String dataType = constant.getConceptDatatype();
                Type thisType = null;
                for (Type type : vrmModel.getTypes()) {
                    if (type.getTypeName().equals(dataType)) {
                        thisType = type;
                        break;
                    }
                }
                if (thisType == null) {
                    BasicErrorRefresh(errorReporter);
                    String outputString = "错误定位：常量" + constant.getConceptName() + "\n错误内容：该常量的数据类型"
                            + constant.getConceptDatatype() + "不存在";
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicConstantType, constant.getConceptName(), outputString));
                } else {
                    String value = constant.getConceptValue();
                    boolean isValueViolateType = isValueNotComplyWithType(thisType, value);
                    if (isValueViolateType) {
                        BasicErrorRefresh(errorReporter);
                        String outputString = "错误定位：常量" + constant.getConceptName() + "\n错误内容：该常量的值不符合数据类型"
                                + constant.getConceptDatatype();
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicConstantValue, constant.getConceptName(), outputString));
                    }
                }
            }
        }
        return errorReporter.isBasicRight();
    }

    /**
     * 检查表格相关变量定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkVariableBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
        for (TableOfVRM table : vrmModel.getConditions()) {
            isTableRelateVariableRight(vrmModel, table, errorReporter);
        }
        for (TableOfVRM table : vrmModel.getEvents()) {
            isTableRelateVariableRight(vrmModel, table, errorReporter);
        }
        return errorReporter.isBasicRight();
    }

    /**
     * 表关联变量是否为存在基础错误
     *
     * @param vrmModel      电压调节模型
     * @param table         表格
     * @param errorReporter 错误报告器
     */
    void isTableRelateVariableRight(VariableRealationModel vrmModel, TableOfVRM table, CheckErrorReporter errorReporter) {
        String[] wrongLocationName = {"数据类型", "值域", "初始值"};
        boolean[] wrongLocation = {true, true, true, true};
        if (table.getRelateVar().getConceptDatatype().isBlank()) {
            wrongLocation[1] = false;
            wrongLocation[0] = false;
        }
        if (table.getRelateVar().getConceptRange().isBlank()) {
            wrongLocation[2] = false;
            wrongLocation[0] = false;
        }
        if (table.getRelateVar().getConceptValue().isBlank()) {
            wrongLocation[3] = false;
            wrongLocation[0] = false;
        }
        if (!wrongLocation[0]) {
            BasicErrorRefresh(errorReporter);
            String outputString = "错误定位：表格" + table.getName() + "\n错误内容：该变量的";
            for (int i = 1; i < 4; i++) {
                if (!wrongLocation[i])
                    outputString += wrongLocationName[i - 1] + "、";
            }
            outputString = outputString.substring(0, outputString.length() - 1);
            outputString += "定义为空";
            errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicVariableMiss, table.getName(), outputString));
        } else {
            String dataType = table.getRelateVar().getConceptDatatype();
            Type thisType = null;
            for (Type type : vrmModel.getTypes()) {
                if (type.getTypeName().equals(dataType))
                    thisType = type;
            }
            if (thisType == null) {
                BasicErrorRefresh(errorReporter);
                String outputString = "错误定位：表格" + table.getName() + "\n错误内容：该变量的数据类型"
                        + table.getRelateVar().getConceptDatatype() + "不存在";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicVariableType, table.getName(), outputString));
            } else {
                String value = table.getRelateVar().getConceptValue();
                boolean isValueViolateType = isValueNotComplyWithType(thisType, value);
                if (isValueViolateType) {
                    BasicErrorRefresh(errorReporter);
                    String outputString = "错误定位：表格" + table.getName() + "\n错误内容：该变量的初始值不符合数据类型"
                            + thisType.getTypeName();
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicVariableInit, table.getName(), outputString));
                }
            }
        }
    }

    /**
     * 检查模式集定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkModeClassBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
        for (ModeClassOfVRM MC : vrmModel.getModeClass()) {
            boolean isInitialDefined = false;
            boolean isModeUndefined = false;
            int initialCount = 0;
            int stateIndex = 0;
            ArrayList<Integer> incompleteIndexes = new ArrayList<Integer>();
            ArrayList<String> initialStates = new ArrayList<String>();
            for (Mode mode : MC.getModes()) {
                stateIndex++;
                if (mode.getModeName().isBlank()) {
                    isModeUndefined = true;
                    if (!incompleteIndexes.contains(Integer.valueOf(stateIndex))) {
                        incompleteIndexes.add(stateIndex);
                    }
                }
                if (mode.getInitialStatus() != null && mode.getInitialStatus() == 1) {
                    initialCount++;
                    isInitialDefined = true;
                    if (!initialStates.contains(mode.getModeName()))
                        initialStates.add(mode.getModeName());
                }
            }
            if (isModeUndefined) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，模式定义不完整";
                String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName() + "\n错误内容：模式表第";
                for (Integer incompleteIndex : incompleteIndexes) {
                    outputString += incompleteIndex + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "行的模式定义不完整";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassMiss, MC.getModeClass().getModeClassName(), outputString));

            } else if (!isInitialDefined) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，无初始模式";
                String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName() + "\n错误内容：模式表不存在初始模式定义";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassNoInit, MC.getModeClass().getModeClassName(), outputString));
            } else if (initialCount > 1) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，初始模式不唯一";
                String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName() + "\n错误内容：定义了多个初始模式";
                for (String initialState : initialStates) {
                    outputString += initialState + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassManyInit, MC.getModeClass().getModeClassName(), outputString));

            } else {
                if (MC.getModeTrans().isEmpty()) {
                    BasicErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，表格为空";
                    String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName() + "\n错误内容：模式转换表为空";
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassTransEmpty, MC.getModeClass().getModeClassName(), outputString));

                } else {
                    ArrayList<String> modesInTable = new ArrayList<String>();
                    ArrayList<String> modesInClass = new ArrayList<String>();
                    ArrayList<String> notExistMode = new ArrayList<String>();
                    for (Mode mode : MC.getModes()) {
                        modesInClass.add(mode.getModeName());
                    }
                    for (StateMachine modeTrans : MC.getModeTrans()) {
                        if (!modesInTable.contains(modeTrans.getSourceState()))
                            modesInTable.add(modeTrans.getSourceState());
                        if (!modesInTable.contains(modeTrans.getEndState()))
                            modesInTable.add(modeTrans.getEndState());
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
                        String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                                + "\n错误内容：模式转换表涉及了模式表中不存在的模式";
                        for (String modeName : notExistMode) {
                            outputString += modeName + "、";
                        }
                        outputString = outputString.substring(0, outputString.length() - 1);
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassModeMiss, MC.getModeClass().getModeClassName(), outputString));

                    } else {
                        boolean isEventWrong = false;
                        boolean isConditionWrong = false;
                        ArrayList<Integer> eventWrongRowNumbers = new ArrayList<Integer>();
                        ArrayList<Integer> conditionWrongRowNumbers = new ArrayList<Integer>();
                        ArrayList<EventItem> events = new ArrayList<>();
                        int currentRowIndex = 0;
                        for (StateMachine mt : MC.getModeTrans()) {
                            currentRowIndex++;
                            String wholeEvent = mt.getEvent();
                            EventTable event = new EventTable();
                            int flag = isEventOrConditionWrong(wholeEvent, event);
                            if (flag == 1) {
                                isEventWrong = true;
                                if (!eventWrongRowNumbers.contains(Integer.valueOf(currentRowIndex)))
                                    eventWrongRowNumbers.add(currentRowIndex);
                            } else if (flag == 2) {
                                isConditionWrong = true;
                                if (!conditionWrongRowNumbers.contains(Integer.valueOf(currentRowIndex)))
                                    conditionWrongRowNumbers.add(currentRowIndex);
                            } else {
                                events.addAll(event.getEvents());
                            }
                        }
                        if (isEventWrong) {
                            BasicErrorRefresh(errorReporter);
                            //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，事件语法错误";
                            String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName() + "\n错误内容：模式转换表第";
                            for (Integer number : eventWrongRowNumbers) {
                                outputString += number.intValue() + "、";
                            }
                            outputString = outputString.substring(0, outputString.length() - 1);
                            outputString += "行的事件语法有误";
                            errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassEvent, MC.getModeClass().getModeClassName(), outputString));

                        } else if (isConditionWrong) {
                            BasicErrorRefresh(errorReporter);
                            //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，条件语法错误";
                            String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName() + "\n错误内容：模式转换表第";
                            for (Integer number : conditionWrongRowNumbers) {
                                outputString += number.intValue() + "、";
                            }
                            outputString = outputString.substring(0, outputString.length() - 1);
                            outputString += "行事件中的条件语法有误";
                            errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassCondition, MC.getModeClass().getModeClassName(), outputString));

                        } else {
                            boolean isVariableUndefined = false;
                            boolean isValueViolateType = false;
                            ArrayList<String> undefinedVariables = new ArrayList<String>();
                            ArrayList<String> violateVariables = new ArrayList<String>();
                            for (EventItem event : events) {
                                for (ConditionTable condition : Arrays.asList(event.getEventCondition(), event.getGuardCondition())) {
                                    if (condition.getAndNum() == 0) {
                                        continue;
                                    }
                                    for (ConditionItem nc : condition.getConditionItems()) {
                                        int flag = isVariableUndefinedOrViolateType(vrmModel, nc.getVar1(), nc.getVar2());
                                        if (flag == 1) {
                                            isVariableUndefined = true;
                                            if (!undefinedVariables.contains(nc.getVar1()))
                                                undefinedVariables.add(nc.getVar1());
                                        }
                                        if (flag == 2) {
                                            isValueViolateType = true;
                                            if (!violateVariables.contains(nc.getVar1()))
                                                violateVariables.add(nc.getVar1());
                                        }
                                    }
                                }
                            }
                            if (isVariableUndefined) {
                                BasicErrorRefresh(errorReporter);
                                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量不存在";
                                String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                                        + "\n错误内容：模式转换表中的变量";
                                for (String undefinedVariable : undefinedVariables) {
                                    outputString += undefinedVariable + "、";
                                }
                                outputString = outputString.substring(0, outputString.length() - 1);
                                outputString += "未在输入变量、中间或输出变量、模式集中定义";
                                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassVariableMiss, MC.getModeClass().getModeClassName(), outputString));

                            } else if (isValueViolateType) {
                                BasicErrorRefresh(errorReporter);
                                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量的值不符合数据类型";
                                String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                                        + "\n错误内容：模式转换表中的变量";
                                for (String violateVariable : violateVariables) {
                                    outputString += violateVariable + "、";
                                }
                                outputString = outputString.substring(0, outputString.length() - 1);
                                outputString += "其逻辑表达式右侧值不符合变量的数据类型";
                                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.BasicModeClassVariableType, MC.getModeClass().getModeClassName(), outputString));
                            }
                        }
                    }
                }
            }
        }
        return errorReporter.isBasicRight();
    }

    /**
     * 检查表格函数定义
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkTableBasic(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
        for (TableOfVRM eventTable : vrmModel.getEvents()) {
            boolean isEventWrong = false;
            boolean isConditionWrong = false;
            ArrayList<Integer> eventWrongRowNumbers = new ArrayList<Integer>();
            ArrayList<Integer> conditionWrongRowNumbers = new ArrayList<Integer>();
            ArrayList<Integer> eventWrongRowReId = new ArrayList<Integer>();
            ArrayList<Integer> conditionWrongRowReId = new ArrayList<Integer>();
            ArrayList<EventItem> events = new ArrayList<>();
            int currentRowIndex = 0;
            // 检查事件表中 语法的错误
            for (TableRow row : eventTable.getRows()) {
                currentRowIndex++;
                String wholeEvent = row.getDetails();
                EventTable table = new EventTable();
                int flag = isEventOrConditionWrong(wholeEvent, table);
                if (flag == 1) {
                    isEventWrong = true;
                    if (!eventWrongRowNumbers.contains(Integer.valueOf(currentRowIndex))) {
                        eventWrongRowNumbers.add(currentRowIndex);
                        eventWrongRowReId.add(row.getRelateReq());
                    }
                }
                if (flag == 2) {
                    isConditionWrong = true;
                    if (!conditionWrongRowNumbers.contains(Integer.valueOf(currentRowIndex))) {
                        conditionWrongRowNumbers.add(currentRowIndex);
                        conditionWrongRowReId.add(row.getRelateReq());
                    }
                } else {
                    events.addAll(table.getEvents());
                }
            }
            if (isEventWrong) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，事件语法错误";
                String outputString = "错误定位：表格" + eventTable.getName() + "\n错误内容：事件表第";
                for (Integer number : eventWrongRowNumbers) {
                    outputString += number.intValue() + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "行的事件语法有误";
                errorReporter.addErrorList(new CheckErrorInfo(
                        errorReporter.getErrorCount(),
                        CheckErrorType.BasicEvent,
                        eventTable.getName(),
                        eventWrongRowReId, outputString));
            } else if (isConditionWrong) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，条件语法错误";
                String outputString = "错误定位：表格" + eventTable.getName() + "\n错误内容：事件表第";
                for (Integer number : conditionWrongRowNumbers) {
                    outputString += number.intValue() + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "行事件中的条件语法有误";
                errorReporter.addErrorList(new CheckErrorInfo(
                        errorReporter.getErrorCount(),
                        CheckErrorType.BasicCondition,
                        eventTable.getName(),
                        conditionWrongRowReId, outputString));
            }
            // 检查事件表中 元素引用的错误
            else {
                boolean isVariableUndefined = false;
                boolean isValueViolateType = false;
                ArrayList<String> undefinedVariables = new ArrayList<String>();
                ArrayList<String> violateVariables = new ArrayList<String>();
                for (EventItem event : events) {
                    for (ConditionTable condition : Arrays.asList(event.getEventCondition(), event.getGuardCondition())) {
                        if (condition.getAndNum() == 0) {
                            continue;
                        }
                        for (ConditionItem nc : condition.getConditionItems()) {
                            int flag = isVariableUndefinedOrViolateType(vrmModel, nc.getVar1(), nc.getVar2());
                            if (flag == 1) {
                                isVariableUndefined = true;
                                if (!undefinedVariables.contains(nc.getVar1()))
                                    undefinedVariables.add(nc.getVar1());
                            }
                            if (flag == 2) {
                                isValueViolateType = true;
                                if (!violateVariables.contains(nc.getVar1()))
                                    violateVariables.add(nc.getVar1());
                            }
                        }
                    }
                }
                if (isVariableUndefined) {
                    BasicErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量不存在";
                    String outputString = "错误定位：表格" + eventTable.getName() + "\n错误内容：事件表中的变量";
                    for (String undefinedVariable : undefinedVariables) {
                        outputString += undefinedVariable + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "未在输入变量、中间或输出变量、模式集中定义";
                    errorReporter.addErrorList(new CheckErrorInfo(
                            errorReporter.getErrorCount(),
                            CheckErrorType.BasicEventVariableMiss,
                            eventTable.getName(),
                            outputString));
                } else if (isValueViolateType) {
                    BasicErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量的值不符合数据类型";
                    String outputString = "错误定位：表格" + eventTable.getName() + "\n错误内容：事件表中的变量";
                    for (String violateVariable : violateVariables) {
                        outputString += violateVariable + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "其逻辑表达式右侧值不符合变量的数据类型";
                    errorReporter.addErrorList(new CheckErrorInfo(
                            errorReporter.getErrorCount(),
                            CheckErrorType.BasicEventVariableType,
                            eventTable.getName(),
                            outputString));
                }
            }
        }
        for (TableOfVRM conditionTable : vrmModel.getConditions()) {
            boolean isConditionWrong = false;
            ArrayList<Integer> conditionWrongRowNumbers = new ArrayList<Integer>();
            ArrayList<Integer> conditionWrongRowReqId = new ArrayList<Integer>();
            ArrayList<ConditionTable> conditions = new ArrayList<>();
            int currentRowIndex = 0;
            for (TableRow row : conditionTable.getRows()) {
                currentRowIndex++;
                String condition = row.getDetails();
                ConditionTable table = new ConditionTable();
                isConditionWrong = isConditionWrong(condition, table);
                if (isConditionWrong) {
                    if (!conditionWrongRowNumbers.contains(currentRowIndex)) {
                        conditionWrongRowNumbers.add(currentRowIndex);
                        conditionWrongRowReqId.add(row.getRelateReq());
                    }
                } else {
                    conditions.add(table);
                }
            }
            if (isConditionWrong) {
                BasicErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，条件语法错误";
                String outputString = "错误定位：表格" + conditionTable.getName() + "\n错误内容：条件表第";
                for (Integer number : conditionWrongRowNumbers) {
                    outputString += number.intValue() + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "行条件语法有误";
                errorReporter.addErrorList(new CheckErrorInfo(
                        errorReporter.getErrorCount(),
                        CheckErrorType.BasicCondition,
                        conditionTable.getName(),
                        conditionWrongRowReqId,
                        outputString));
            } else {
                boolean isVariableUndefined = false;
                boolean isValueViolateType = false;
                ArrayList<String> undefinedVariables = new ArrayList<String>();
                ArrayList<String> violateVariables = new ArrayList<String>();
                for (ConditionTable condition : conditions) {
                    if (condition.getAndNum() == 0) {
                        continue;
                    }
                    for (ConditionItem nc : condition.getConditionItems()) {
                        int flag = isVariableUndefinedOrViolateType(vrmModel, nc.getVar1(), nc.getVar2());
                        if (flag == 1) {
                            isVariableUndefined = true;
                            if (!undefinedVariables.contains(nc.getVar1()))
                                undefinedVariables.add(nc.getVar1());
                        }
                        if (flag == 2) {
                            isValueViolateType = true;
                            if (!violateVariables.contains(nc.getVar1()))
                                violateVariables.add(nc.getVar1());
                        }
                    }
                }
                if (isVariableUndefined) {
                    BasicErrorRefresh(errorReporter);
                    String outputString = "错误定位：表格" + conditionTable.getName() + "\n错误内容：条件表中的变量";
                    for (String undefinedVariable : undefinedVariables) {
                        outputString += undefinedVariable + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "未在输入变量、中间或输出变量、模式集中定义";
                    errorReporter.addErrorList(new CheckErrorInfo(
                            errorReporter.getErrorCount(),
                            CheckErrorType.BasicConditionVariableMiss,
                            conditionTable.getName(),
                            outputString));
                } else if (isValueViolateType) {
                    BasicErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：基本范式检查，涉及变量的值不符合数据类型";
                    String outputString = "错误定位：表格" + conditionTable.getName() + "\n错误内容：条件表中的变量";
                    for (String violateVariable : violateVariables) {
                        outputString += violateVariable + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "其逻辑表达式右侧值不符合变量的数据类型";
                    errorReporter.addErrorList(new CheckErrorInfo(
                            errorReporter.getErrorCount(),
                            CheckErrorType.BasicConditionVariableType,
                            conditionTable.getName(),
                            outputString));
                }
            }
        }
        return false;
    }

    @Resource
    DataTypeUtils dataTypeUtils;

    /**
     * 检查值与类型是否匹配，用于基础语法规范检查
     */
    boolean isValueNotComplyWithType(Type type, String value) {
        boolean isNotComply = false;
        String range = type.getTypeRange();
        String baseType = type.getDataType();
        try {
            if (baseType.equalsIgnoreCase(BasicDataType.StringType.getTypeName())) {

            } else if (baseType.equalsIgnoreCase(BasicDataType.IntegerType.getTypeName())) {
                // 分离区间边界
                List<Integer> ranges = dataTypeUtils.GetRangeList(type);
                int asciiOfChar = Integer.valueOf(value);
                if (asciiOfChar < ranges.get(0) || asciiOfChar > ranges.get(1))
                        isNotComply = true;
            } else if (baseType.equalsIgnoreCase(BasicDataType.DoubleType.getTypeName())) {
                List<Double> ranges = dataTypeUtils.GetRangeList(type);
                double vValue = Double.parseDouble(value);
                if (vValue < ranges.get(0) || vValue > ranges.get(1))
                    isNotComply = true;
            } else if (baseType.equalsIgnoreCase(BasicDataType.FloatType.getTypeName())) {
                List<Float> ranges = dataTypeUtils.GetRangeList(type);
                double vValue = Float.parseFloat(value);
                if (vValue < ranges.get(0) || vValue > ranges.get(1))
                    isNotComply = true;
            } else {
                boolean isValueExist = false;
                List<String> ranges = dataTypeUtils.GetRangeList(type);
                for (String singleRange : ranges) {
                    if (singleRange.equals(value)) {
                        isValueExist = true;
                        break;
                    }
                }
                if (!isValueExist)
                    isNotComply = true;
            }
        } catch (Exception ex) {
            isNotComply = true;
            ex.printStackTrace();
        }
        return isNotComply;
    }


    /**
     * 检查事件语法定义是否符合基本语法要求
     *
     * @return 1 事件语法错误
     * @return 2 条件语法错误
     */
    int isEventOrConditionWrong(String wholeEvent, EventTable Returned) {
        EventTable eventTable = null;
        try {
            eventTable = eventTableUtils.ConvertStringToTable(wholeEvent);
            Returned = eventTable;
        } catch (Exception e) {
            if (e.getMessage().contains("condition")) {
                return 2;
            } else return 1;
        }
        return 0;
    }


    /**
     * 检查条件是否符合语法规范
     *
     * @return boolean
     */
    boolean isConditionWrong(String condition, ConditionTable Returned) {
        boolean isConditionWrong = false;
        ConditionTable conditionTable = null;
        try {
            conditionTable = conditionTableUtils.ConvertStringToTable(condition);
            Returned = conditionTable;
        } catch (Exception e) {
            isConditionWrong = true;
        }
        return isConditionWrong;
    }

    /**
     * 检查变量是否定义或者取值是否合法，用于基础语法检查
     *
     * @param vrmModel      vrm模型
     * @param variable      变量
     * @param variableValue 变量值
     * @return 1 变量不存在
     * @return 2 变量类型错误
     */
    int isVariableUndefinedOrViolateType(VariableRealationModel vrmModel, String variable, String variableValue) {
        boolean isVariableExist = false;
        boolean isCurrentValueViolateType = false;
        for (ConceptLibrary var : vrmModel.getInputs()) {
            if (var.getConceptName().equals(variable)) {
                isVariableExist = true;
                for (Type type : vrmModel.getTypes()) {
                    if (type.getTypeName().equals(var.getConceptDatatype())) {
                        isCurrentValueViolateType = isValueNotComplyWithType(type, variableValue);
                        break;
                    }
                }
                break;
            }
        }
        for (ConceptLibrary var : vrmModel.getTerms()) {
            if (var.getConceptName().equals(variable)) {
                isVariableExist = true;
                for (Type type : vrmModel.getTypes()) {
                    if (type.getTypeName().equals(var.getConceptDatatype())) {
                        isCurrentValueViolateType = isValueNotComplyWithType(type, variableValue);
                        break;
                    }
                }
                break;
            }
        }
        for (ConceptLibrary var : vrmModel.getOutputs()) {
            if (var.getConceptName().equals(variable)) {
                isVariableExist = true;
                for (Type type : vrmModel.getTypes()) {
                    if (type.getTypeName().equals(var.getConceptDatatype())) {
                        isCurrentValueViolateType = isValueNotComplyWithType(type, variableValue);
                        break;
                    }
                }
                break;
            }
        }

        for (ModeClassOfVRM MC : vrmModel.getModeClass()) {
            if (variable.equals(MC.getModeClass().getModeClassName())) {
                isVariableExist = true;
                for (Mode mode : MC.getModes()) {
                    if (mode.getModeName().equals(variableValue)) {
                        isCurrentValueViolateType = true;
                        break;
                    }
                }
                break;
            }

        }
        if (!isVariableExist)
            return 1;
        else if (isCurrentValueViolateType)
            return 2;
        else
            return 0;
    }

}

