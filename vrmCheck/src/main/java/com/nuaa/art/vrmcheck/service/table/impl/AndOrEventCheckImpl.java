package com.nuaa.art.vrmcheck.service.table.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.common.ConceptItemType;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.model.vrm.ModeClassOfVRM;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.common.utils.OutputUtils;
import com.nuaa.art.vrmcheck.model.scenario.Scenario;
import com.nuaa.art.vrmcheck.model.table.AndOrEventsInformation;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.table.EventCheck;
import com.nuaa.art.vrmcheck.service.table.ScenarioHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("AndOrEvent")
public class AndOrEventCheckImpl implements EventCheck {

    @Resource
    AndOrEventParserImpl eventPraser;

    @Resource(name = "ScenarioV2")
    ScenarioHandler scenarioHandler;

    /**
     * 检查事件一致性
     *
     * @param vrmModel      电压调节模型
     * @param errorReporter 错误报告器
     * @return boolean
     */
    @Override
    public boolean checkEventConsistency(VRM vrmModel, CheckErrorReporter errorReporter) {
        for (TableOfVRM event: vrmModel.getEvents()) {
            // 获取在表中出现的所有源模式
            List<String> modes = event.getRows().stream().map(TableRow::getMode).distinct().collect(Collectors.toList());

            for (String sourceMode : modes) {// 对同模式的条件进行一致性完整性判断
                List<TableRow> subTable = new ArrayList<>();
                ArrayList<Integer> wrongRowReqID = new ArrayList<Integer>();// 对应每行的需求编号
                for (TableRow row : event.getRows()) {
                    if (row.getMode().equals(sourceMode)) {
                        subTable.add(row);
                        wrongRowReqID.add(row.getRelateReq());
                    }
                }
                LogUtils.debug("事件"+subTable.stream().map(TableRow::getDetails).collect(Collectors.joining()));

                AndOrEventsInformation ei = eventPraser.emptyEventsInformationFactory();
                eventPraser.setParentRangeAndValueOfEachRow(vrmModel, event, ei);
                eventPraser.praserInformationInTables(vrmModel, subTable, ei);
                ei.scenarioCorpusCoder = scenarioHandler.constructScenarioCorpus(ei.criticalVariables.size(), ei.variableRanges);
                scenarioHandler.buildEquivalentScenarioSetPair(ei);

                int specialError = checkSpeciaError(ei);
                ArrayList<EventConsistencyError> errors = checkSubEventTableConsistency(ei);

                if(specialError != 0){
                    if(specialError == 4){
                        String outputString = "";
                        errorReporter.addErrorCount();
                        outputString = outputString = "错误定位：表格" + event.getName() + "\n系统："+vrmModel.getSystem().getSystemName()
                                + "\n错误内容：";
                        if (!sourceMode.isBlank())
                            outputString += "处于模式" + sourceMode + "下时，\n";
                        outputString +="同时存在永真事件和默认事件，\n会导致";
                        if (event.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
                            outputString += "输出变量";
                        else
                            outputString += "中间变量";
                        outputString += "同时取";
                        for(int i  = 0 ; i < ei.outputRanges.size(); i++){
                            if(ei.rowsForTrueScenarioSet.contains(i) || ei.rowsForDefaultScenarioSet.contains(i))
                                outputString += ei.outputRanges.get(i)+"、";
                        }
                        outputString = outputString.substring(0,outputString.length()-1);

                        errorReporter.addErrorList(
                                new CheckErrorInfo(errorReporter.getErrorCount(),
                                        CheckErrorType.EventConsistencyTrue,
                                        event.getName(),
                                        outputString));
                    }
                    if(specialError == 1 || specialError == 3){
                        String outputString = "";
                        errorReporter.addErrorCount();
                        outputString = outputString = "错误定位：表格" + event.getName() + "\n系统："+vrmModel.getSystem().getSystemName()
                                + "\n错误内容：";
                        if (!sourceMode.isBlank())
                            outputString += "处于模式" + sourceMode + "下时，\n";
                        outputString +="同时存在多个永真事件，\n会导致";
                        if (event.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
                            outputString += "输出变量";
                        else
                            outputString += "中间变量";
                        outputString += "同时取";
                        for(int i  = 0 ; i < ei.outputRanges.size(); i++){
                            if(ei.rowsForTrueScenarioSet.contains(i) || ei.rowsForDefaultScenarioSet.contains(i))
                                outputString += ei.outputRanges.get(i)+"、";
                        }
                        outputString = outputString.substring(0,outputString.length()-1);

                        errorReporter.addErrorList(
                                new CheckErrorInfo(errorReporter.getErrorCount(),
                                        CheckErrorType.EventConsistencyTrue,
                                        event.getName(),
                                        outputString));
                    }
                    if(specialError == 2 || specialError == 3){
                        String outputString = "";
                        errorReporter.addErrorCount();
                        outputString = outputString = "错误定位：表格" + event.getName() + "\n系统："+vrmModel.getSystem().getSystemName()
                                + "\n错误内容：";
                        if (!sourceMode.isBlank())
                            outputString += "处于模式" + sourceMode + "下时，\n";
                        outputString +="同时存在多个默认事件，\n会导致";
                        if (event.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
                            outputString += "输出变量";
                        else
                            outputString += "中间变量";
                        outputString += "同时取";
                        for(int i  = 0 ; i < ei.outputRanges.size(); i++){
                            if(ei.rowsForTrueScenarioSet.contains(i) || ei.rowsForDefaultScenarioSet.contains(i))
                                outputString += ei.outputRanges.get(i)+"、";
                        }
                        outputString = outputString.substring(0,outputString.length()-1);

                        errorReporter.addErrorList(
                                new CheckErrorInfo(errorReporter.getErrorCount(),
                                        CheckErrorType.EventConsistencyTrue,
                                        event.getName(),
                                        outputString));
                    }
                } else if(!errors.isEmpty()){
                    String variableSet = OutputUtils.getVariableSetHeader(ei.criticalVariables.continualVariables, ei.criticalVariables.discreteVariables);
                    String outputString = "";// 输出文本
                    errorReporter.setEventRight(false);
                    for (EventConsistencyError eces : errors) {// 遍历每一组冲突
                        for(ArrayList<Scenario>[] obeyScenario: eces.obeyScenarios){
                            errorReporter.addErrorCount();
                            //errorString = "错误" + errorCount + "-错误类型：第三范式检查，违反事件一致性";
                            outputString = "错误定位：表格" + event.getName() + "\n系统："+vrmModel.getSystem().getSystemName()
                                    + "\n错误内容：";
                            if (!sourceMode.isBlank())
                                outputString += "处于模式" + sourceMode + "下时，\n";
                            outputString += "当变量取值从下列两表前者任意一行的组合变换到后者任意一行的组合时，\n";
                            if (event.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
                                outputString += "输出变量";
                            else
                                outputString += "中间变量";
                            outputString += "同时取" + eces.assignment[0] + "和"
                                    + eces.assignment[1] + "\n" + variableSet;
                            for (Scenario cs : obeyScenario[0]) {
                                outputString += "|";
                                for (String value : scenarioHandler.getScenarioDetails(ei.criticalVariables,cs)) {
                                    outputString += String.format("%-15s", value) + "|";
                                }
                                outputString += "\n";
                            }
                            outputString += "\n";
                            outputString += variableSet;
                            for (Scenario cs : obeyScenario[1]) {
                                outputString += "|";
                                for (String value : scenarioHandler.getScenarioDetails(ei.criticalVariables,cs)) {
                                    outputString += String.format("%-15s", value) + "|";
                                }
                                outputString += "\n";
                            }
                            outputString = outputString.substring(0,
                                    outputString.length() - 1);

                            errorReporter.addErrorList(new CheckErrorInfo(
                                    errorReporter.getErrorCount(),
                                    CheckErrorType.EventConsistencyValue,
                                    event.getName(),
                                    wrongRowReqID,
                                    event.getRelateVar().getConceptDependencyModeClass(),
                                    outputString));
                        }
                    }
                }

            }
        }

        return errorReporter.isEventRight();
    }

    /**
     * 检查模式转换一致性
     *
     * @param vrmModel      VRM模型
     * @param errorReporter 错误报告器
     * @return boolean
     */
    @Override
    public boolean checkModeTransConsistency(VRM vrmModel, CheckErrorReporter errorReporter) {
        for (ModeClassOfVRM MC: vrmModel.getModeClass()) {
            // 获取在表中出现的所有源模式
            List<String> modes = MC.getModeTrans().stream().map(StateMachine::getSourceState).distinct().collect(Collectors.toList());

            for (String sourceMode : modes) {// 对同模式的条件进行一致性完整性判断

                List<TableRow> subTable = new ArrayList<>();
                //ArrayList<Integer> wrongRowReqID = new ArrayList<Integer>();// 对应每行的需求编号
                for (StateMachine row : MC.getModeTrans()) {
                    if (row.getSourceState().equals(sourceMode)) {
                        TableRow thisRow = new TableRow();
                        thisRow.setAssignment(row.getEndState());
                        thisRow.setDetails(row.getEvent());
                        subTable.add(thisRow);
                        //System.err.println(thisRow.getDetails());
                        //wrongRowReqID.add(row.getRelateReq());
                    }
                }

                AndOrEventsInformation ei = eventPraser.emptyEventsInformationFactory();
                eventPraser.setParentRangeAndValueOfEachRow(vrmModel, MC, subTable, ei);
                eventPraser.praserInformationInTables(vrmModel, subTable, ei);
                ei.scenarioCorpusCoder = scenarioHandler.constructScenarioCorpus(ei.criticalVariables.size(), ei.variableRanges);
                scenarioHandler.buildEquivalentScenarioSetPair(ei);
                int specialError = checkSpeciaError(ei);
                ArrayList<EventConsistencyError> errors = checkSubEventTableConsistency(ei);

                if(specialError != 0){
                    if(specialError == 4){
                        String outputString = "";
                        errorReporter.addErrorCount();
                        outputString = outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                                + "\n错误内容：同时存在永真事件和默认事件，\n会导致同时从源模式"
                                + sourceMode + "转换到不同的目标模式：";
                        errorReporter.addErrorList(
                                new CheckErrorInfo(errorReporter.getErrorCount(),
                                CheckErrorType.EventConsistencyModeTransTrue,
                                        MC.getModeClass().getModeClassName(),
                                        outputString));
                    }
                    if(specialError == 1 || specialError == 3){
                        String outputString = "";
                        errorReporter.addErrorCount();
                        outputString = outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                                + "\n错误内容：存在多个永真模式转换事件，\n会导致同时从源模式"
                                + sourceMode + "转换到不同的目标模式：";
                        errorReporter.addErrorList(
                                new CheckErrorInfo(errorReporter.getErrorCount(),
                                        CheckErrorType.EventConsistencyModeTransTrue,
                                        MC.getModeClass().getModeClassName(),
                                        outputString));
                    }
                    if(specialError == 2 || specialError == 3){
                        String outputString = "";
                        errorReporter.addErrorCount();
                        outputString = outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                                + "\n错误内容：存在多个默认模式转换事件，\n会导致同时从源模式"
                                + sourceMode + "转换到不同的目标模式：";
                        errorReporter.addErrorList(
                                new CheckErrorInfo(errorReporter.getErrorCount(),
                                        CheckErrorType.EventConsistencyModeTransTrue,
                                        MC.getModeClass().getModeClassName(),
                                        outputString));
                    }
                }
                else if(!errors.isEmpty()){
                    String variableSet = OutputUtils.getVariableSetHeader(ei.criticalVariables.continualVariables, ei.criticalVariables.discreteVariables);
                    String outputString = "";// 输出文本
                    errorReporter.setEventRight(false);
                    for (EventConsistencyError ece : errors) {// 遍历每一组冲突
                        for(var obeyScenarios: ece.obeyScenarios){
                            errorReporter.addErrorCount();
                            outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                                    + "\n错误内容：当变量取值从下列两表中前者任意一行的组合变换为后者任意一行的组合时，\n会同时从源模式"
                                    + sourceMode + "转换到不同的目标模式" + ece.assignment[0] + "和"
                                    + ece.assignment[1] + "\n" + variableSet;
                            for (Scenario cs : obeyScenarios[0]) {
                                outputString += "|";
                                for (String value : scenarioHandler.getScenarioDetails(ei.criticalVariables,cs)) {
                                    outputString += String.format("%-15s", value) + "|";
                                }
                                outputString += "\n";
                            }
                            outputString += "\n";
                            outputString += variableSet;
                            for (Scenario cs : obeyScenarios[1]) {
                                outputString += "|";
                                for (String value : scenarioHandler.getScenarioDetails(ei.criticalVariables,cs)) {
                                    outputString += String.format("%-15s", value) + "|";
                                }
                                outputString += "\n";
                            }
                            outputString = outputString.substring(0, outputString.length() - 1);
                            errorReporter.addErrorList(new CheckErrorInfo(
                                    errorReporter.getErrorCount(),
                                    CheckErrorType.EventConsistencyModeTrans,
                                    MC.getModeClass().getModeClassName(),
                                    outputString));
                        }
                    }
                }

            }
        }

        return errorReporter.isEventRight();
    }


    public class EventConsistencyError {
        public String[] assignment = new String[2];
        public ArrayList<ArrayList<Scenario>[]> obeyScenarios = new ArrayList<>();

    }

    //todo 对True类型事件； default类型事件等价场景集的分析。 如果等价场景序偶不完整？应该怎么定义
    public ArrayList<EventConsistencyError> checkSubEventTableConsistency(AndOrEventsInformation ei) {
        //ArrayList<Integer> row = new ArrayList<>(ei.scenarioSetPairsOfEachRow.keySet());
        int rowCount = ei.outputRanges.size();
        ArrayList<EventConsistencyError> consistencyErrors = new ArrayList<>();
        if (ei.criticalVariables.size() != 0) {
            for (int i = 0; i < rowCount; i++) {
                for (int j = i + 1; j < rowCount; j++) {
                    String assignmentOne = ei.outputRanges.get(i);
                    String assignmentTwo = ei.outputRanges.get(j);
                    EventConsistencyError ece = new EventConsistencyError();
                    ece.assignment[0] = assignmentOne;
                    ece.assignment[1] = assignmentTwo;
                    ArrayList<ArrayList<Long>[]> collectionOne = ei.scenarioSetPairsOfEachRow.get(i);
                    ArrayList<ArrayList<Long>[]> collectionTwo = ei.scenarioSetPairsOfEachRow.get(j);
                    if(ei.rowsForTrueScenarioSet.contains(i) || ei.rowsForTrueScenarioSet.contains(j)){ //处理永真行
                        ArrayList<ArrayList<Long>[]> collection = !collectionOne.isEmpty()? collectionOne : collectionTwo;
                        for(var setPairs: collection){
                            if(!setPairs[0].isEmpty() && !setPairs[1].isEmpty()){
                                ArrayList<Scenario> pre = new ArrayList<>();
                                ArrayList<Scenario> next = new ArrayList<>();
                                for(Long one: setPairs[0]){
                                    pre.add(ei.scenarioCorpusCoder.decode(one));
                                }
                                for(Long two: setPairs[1]){
                                    next.add(ei.scenarioCorpusCoder.decode(two));
                                }
                                if(!pre.isEmpty() && !next.isEmpty()){
                                    ArrayList<Scenario>[] obeyScenarios= new ArrayList[2];
                                    obeyScenarios[0] = pre;
                                    obeyScenarios[1] = next;
                                    ece.obeyScenarios.add(obeyScenarios);
                                }

                            }
                        }
                    } else {
                        for (var setPairsOfOne: collectionOne) {
                            for(var setPairsOfTwo: collectionTwo){
                                ArrayList<Long>[] obeyset= new ArrayList[2];
                                obeyset[0] = (ArrayList<Long>) setPairsOfOne[0].stream()
                                        .filter((scenarios)-> setPairsOfTwo[0].contains(scenarios)).collect(Collectors.toList());
                                obeyset[1] = (ArrayList<Long>) setPairsOfOne[1].stream()
                                        .filter((scenarios)-> setPairsOfTwo[1].contains(scenarios)).collect(Collectors.toList());
                                if(!obeyset[0].isEmpty() && !obeyset[1].isEmpty()){
                                    ArrayList<Scenario> pre = new ArrayList<>();
                                    ArrayList<Scenario> next = new ArrayList<>();
                                    for(Long one: obeyset[0]){
                                        pre.add(ei.scenarioCorpusCoder.decode(one));
                                    }
                                    for(Long two: obeyset[1]){
                                        next.add(ei.scenarioCorpusCoder.decode(two));
                                    }
                                    if(!pre.isEmpty() && !next.isEmpty()){
                                        ArrayList<Scenario>[] obeyScenarios= new ArrayList[2];
                                        obeyScenarios[0] = pre;
                                        obeyScenarios[1] = next;
                                        ece.obeyScenarios.add(obeyScenarios);
                                    }
                                }
                            }
                        }
                    }
                    if(!ece.obeyScenarios.isEmpty())
                        consistencyErrors.add(ece);
                }
            }
        }
        return consistencyErrors;
    }

    // 对多个永真永假式的判断。1:永真式过多。 2： default式过多 3：两种问题均出现 4:在限定事件不为空的情况下，
    public int checkSpeciaError(AndOrEventsInformation ei){
        int res = 0;
        if(ei.criticalVariables.size()>0){ //有限定事件的情况。
            if(!ei.rowsForTrueScenarioSet.isEmpty() && !ei.rowsForDefaultScenarioSet.isEmpty()) {
                res = 4;
                return res;
            }
            if(ei.rowsForTrueScenarioSet.size()>1) res +=1;
            if(ei.rowsForDefaultScenarioSet.size()>1) res += 2;
            return  res;
        } else { //再没有给定一般条件的情况下，Default的语义和True语义一致了。
            if(ei.rowsForTrueScenarioSet.size() + ei.rowsForDefaultScenarioSet.size() > 1) res +=1;
            return res;
        }

    }
}
