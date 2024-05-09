package com.nuaa.art.vrmcheck.service.table.impl;

import com.nuaa.art.vrm.entity.Mode;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.table.InputCheck;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 输入完整性检查
 *
 * @author konsin
 * @date 2023/09/12
 */
@Service
public class InputCheckImpl implements InputCheck {
    /**
     * 检查条件表输入完整性
     *
     * @param vrmModel      vrm模型
     * @param errorReporter 错误报告
     * @return boolean
     */
    @Override
    public boolean checkInputIntegrityOfCondition(VRM vrmModel, CheckErrorReporter errorReporter) {
        for (TableOfVRM table: vrmModel.getConditions()) {
            checkInputIntegrity(vrmModel,table,false,errorReporter);
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
    public boolean checkInputIntegrityOfEvent(VRM vrmModel, CheckErrorReporter errorReporter) {
        for (TableOfVRM table: vrmModel.getEvents()) {
            checkInputIntegrity(vrmModel,table,true,errorReporter);
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
    public boolean checkInputIntegrityOfModeTrans(VRM vrmModel, CheckErrorReporter errorReporter) {
        for (ModeClassOfVRM MC: vrmModel.getModeClass()){
            ArrayList<String> modesInClass = (ArrayList<String>) MC.getModes().stream().map(Mode::getModeName).filter(str->!str.isBlank()).collect(Collectors.toList());
            ArrayList<String> modesInTable = (ArrayList<String>) MC.getModeTrans().stream().map(StateMachine::getSourceState).filter(str->!str.isBlank()).collect(Collectors.toList());
//            System.out.println(modesInTable);
//            System.out.println(modesInClass);
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
                String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                        + "\n错误内容：该模式集模式转换表中的模式";
                for (String modeNotExist : modesNotExist) {
                    outputString += modeNotExist + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "未在模式集中定义";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.InputIntegrityModeTransModeMiss, MC.getModeClass().getModeClassName(), outputString));
            }
            if (isModeNotAll) {
                InputErrorRefresh(errorReporter);
                //String errorString = "错误" + errorCount + "-错误类型：第一范式检查，违反输入完整性";
                String outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                        + "\n错误内容：没有离开该模式集的模式";
                for (String modeNotContained : modesNotContained) {
                    outputString += modeNotContained + "、";
                }
                outputString = outputString.substring(0, outputString.length() - 1);
                outputString += "的模式转换";
                errorReporter.addErrorList(new CheckErrorInfo(errorReporter.getErrorCount(), CheckErrorType.InputIntegrityModeTransNextMiss, MC.getModeClass().getModeClassName(), outputString));
            }
        }
        return errorReporter.isInputIntegrityRight();
    }


    /**
     * 检查输入完整性(模式依赖完整性？)
     *
     * @param vrmModel      vrm模型
     * @param table         表格
     * @param errorReporter 错误报告器
     * @param isEvent       是否是事件表
     */
    void checkInputIntegrity(VRM vrmModel, TableOfVRM table, Boolean isEvent, CheckErrorReporter errorReporter){
        String referencedStateMachine = table.getRelateVar().getConceptDependencyModeClass();
        // 输入完整性
        if (referencedStateMachine != null && (!referencedStateMachine.isBlank())) {
            ArrayList<String> modesInTable;
            ArrayList<String> modesInClass;
            ModeClassOfVRM depend = null;

            for(ModeClassOfVRM MC: vrmModel.getModeClass()) {// 找到模式依赖
                if (MC.getModeClass().getModeClassName().equals(referencedStateMachine))
                    depend = MC;
            }
            if (depend == null) {
                InputErrorRefresh(errorReporter);
                String outputString = "错误定位：表格" + table.getName()+ "\n系统："+vrmModel.getSystem().getSystemName() + "\n错误内容：该表格依赖的模式集"
                        + referencedStateMachine + "未在模式集字典中定义";
                if(isEvent){
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityEventModeClassMiss,table.getName(),outputString));
                } else {
                    errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityConditionModeClassMiss,table.getName(),outputString));
                }
            } else {
                // 获取表所具体关联的模式们
                modesInTable = (ArrayList<String>) table.getRows().stream().map(TableRow::getMode).filter(str-> !str.isBlank()).collect(Collectors.toList());
                //获取依赖模式下所有模式
                modesInClass = (ArrayList<String>) depend.getModes().stream().map(Mode::getModeName).filter(str->!str.isBlank()).collect(Collectors.toList());
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
                    String outputString = "错误定位：表格" + table.getName() + "\n系统："+vrmModel.getSystem().getSystemName() + "\n错误内容：该表格依赖的模式";
                    for (String modeNotExist : modesNotExist) {
                        outputString += modeNotExist + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "未在依赖的模式集中定义";
                    if(isEvent){
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityEventModeMiss, table.getName(), outputString));
                    } else {
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityConditionModeMiss,table.getName(),outputString));
                    }
                }
                if (isModeNotAll) {
                    InputErrorRefresh(errorReporter);
                    //String errorString = "错误" + errorCount + "-错误类型：第一范式检查，违反输入完整性";
                    String outputString = "错误定位：表格" + table.getName() + "\n系统："+vrmModel.getSystem().getSystemName() + "\n错误内容：该表格依赖的模式集的模式";
                    for (String modeNotContained : modesNotContained) {
                        outputString += modeNotContained + "、";
                    }
                    outputString = outputString.substring(0, outputString.length() - 1);
                    outputString += "未在表格中涉及";
                    if(isEvent){
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityEventModeUnmeet,table.getName(),outputString));
                    } else {
                        errorReporter.addErrorList(new CheckErrorInfo(errorReporter.errorCount, CheckErrorType.InputIntegrityConditionModeUnmeet,table.getName(),outputString));
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
