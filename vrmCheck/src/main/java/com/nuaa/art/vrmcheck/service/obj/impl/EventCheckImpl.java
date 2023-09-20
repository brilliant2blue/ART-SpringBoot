package com.nuaa.art.vrmcheck.service.obj.impl;

import com.nuaa.art.vrm.common.ConceptItemType;
import com.nuaa.art.vrm.entity.StateMachine;
import com.nuaa.art.vrm.model.*;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.common.utils.OutputUtils;
import com.nuaa.art.vrmcheck.model.*;
import com.nuaa.art.vrmcheck.model.obj.EventsInformation;
import com.nuaa.art.vrmcheck.service.obj.EventCheck;
import com.nuaa.art.vrmcheck.service.obj.EventPraser;
import com.nuaa.art.vrmcheck.service.obj.ScenarioHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventCheckImpl implements EventCheck {
    @Resource
    EventPraser eventPraser;
    @Resource
    ScenarioHandler scenarioHandler;

    /**
     * 检查事件一致性
     *
     * @param vrmModel      电压调节模型
     * @param errorReporter 错误报告器
     * @return boolean
     */
    @Override
    public boolean checkEventConsistency(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
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

                EventsInformation ei = eventPraser.emptyEventsInformationFactory();
                eventPraser.praserInformationInTables(vrmModel, subTable, ei);
                ei.scenarioCorpusCoder = scenarioHandler.constructScenarioCorpus(ei.continualVariables.size() + ei.discreteVariables.size(), ei.variableRanges);
                scenarioHandler.buildEquivalentScenarioSetPair(ei);
                ArrayList<EventConsistencyError> errors = checkSubEventTableConsistency(ei);


                if(!errors.isEmpty()){
                    String variableSet = OutputUtils.getVariableSetHeader(ei.continualVariables, ei.discreteVariables);
                    String outputString = "";// 输出文本
                    errorReporter.setEventRight(false);
                    for (EventConsistencyError ece : errors) {// 遍历每一组冲突
                        errorReporter.addErrorCount();
                        //errorString = "错误" + errorCount + "-错误类型：第三范式检查，违反事件一致性";
                        outputString = "错误定位：表格" + event.getName()
                                + "\n错误内容：";
                        if (!sourceMode.isBlank())
                            outputString += "处于模式" + sourceMode + "下时，\n";
                        outputString += "当变量取值从下列两表前者任意一行的组合变换到后者任意一行的组合时，\n";
                        if (event.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
                            outputString += "输出变量";
                        else
                            outputString += "中间变量";
                        outputString += "同时取" + ece.assignment[0] + "和"
                                + ece.assignment[1] + "\n" + variableSet;
                        for (Scenario cs : ece.obeyScenarios[0]) {
                            outputString += "|";
                            for (String value : scenarioHandler.getScenarioDetails(ei,cs)) {
                                outputString += String.format("%-15s", value) + "|";
                            }
                            outputString += "\n";
                        }
                        outputString += "\n";
                        outputString += variableSet;
                        for (Scenario cs : ece.obeyScenarios[1]) {
                            outputString += "|";
                            for (String value : scenarioHandler.getScenarioDetails(ei,cs)) {
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
    public boolean checkModeTransConsistency(VariableRealationModel vrmModel, CheckErrorReporter errorReporter) {
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
                        //wrongRowReqID.add(row.getRelateReq());
                    }
                }

                EventsInformation ei = eventPraser.emptyEventsInformationFactory();
                eventPraser.praserInformationInTables(vrmModel, subTable, ei);
                ei.scenarioCorpusCoder = scenarioHandler.constructScenarioCorpus(ei.continualVariables.size() + ei.discreteVariables.size(), ei.variableRanges);
                scenarioHandler.buildEquivalentScenarioSetPair(ei);
                ArrayList<EventConsistencyError> errors = checkSubEventTableConsistency(ei);


                if(!errors.isEmpty()){
                    String variableSet = OutputUtils.getVariableSetHeader(ei.continualVariables, ei.discreteVariables);
                    String outputString = "";// 输出文本
                    errorReporter.setEventRight(false);
                    for (EventConsistencyError ece : errors) {// 遍历每一组冲突
                        errorReporter.addErrorCount();
                        outputString = "错误定位：模式集" + MC.getModeClass().getModeClassName()
                                + "\n错误内容：当变量取值从下列两表中前者任意一行的组合变换为后者任意一行的组合时，\n会同时从源模式"
                                + sourceMode + "转换到不同的目标模式" + ece.assignment[0] + "和"
                                + ece.assignment[1] + "\n" + variableSet;
                        for (Scenario cs : ece.obeyScenarios[0]) {
                            outputString += "|";
                            for (String value : scenarioHandler.getScenarioDetails(ei,cs)) {
                                outputString += String.format("%-15s", value) + "|";
                            }
                            outputString += "\n";
                        }
                        outputString += "\n";
                        outputString += variableSet;
                        for (Scenario cs : ece.obeyScenarios[1]) {
                            outputString += "|";
                            for (String value : scenarioHandler.getScenarioDetails(ei,cs)) {
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

        return errorReporter.isEventRight();
    }

    public class EventConsistencyError {
        public String[] assignment = new String[2];
        public ArrayList<Scenario>[] obeyScenarios = new ArrayList[2];

        public EventConsistencyError() {
            obeyScenarios[0] = new ArrayList<Scenario>();
            obeyScenarios[1] = new ArrayList<Scenario>();
        }
    }

    public ArrayList<EventConsistencyError> checkSubEventTableConsistency(EventsInformation ei) {
        ArrayList<EventConsistencyError> consistencyErrors = new ArrayList<>();
        if (ei.continualVariables.size() + ei.discreteVariables.size() != 0) {
            for (int i = 0; i < ei.scenarioSetPair.size(); i++) {
                for (int j = i + 1; j < ei.scenarioSetPair.size(); j++) {
                    ArrayList<Long>[] collectionOne = ei.scenarioSetPair.get(i);
                    ArrayList<Long>[] collectionTwo = ei.scenarioSetPair.get(j);
                    String assignmentOne = ei.assignmentForEachRow.get(i);
                    String assignmentTwo = ei.assignmentForEachRow.get(j);
                    EventConsistencyError ece = new EventConsistencyError();
                    ece.assignment[0] = assignmentOne;
                    ece.assignment[1] = assignmentTwo;
                    if (!assignmentOne.equals(assignmentTwo)) {
                        for (int l = 0; l < collectionOne[0].size(); l++) {
                            if (collectionTwo[0].contains(collectionOne[0].get(l))) {
                                Scenario scenario = ei.scenarioCorpusCoder.decode(collectionOne[0].get(l));
                                ece.obeyScenarios[0].add(scenario);
                            }
                        }
                        for (int l = 0; l < collectionOne[1].size(); l++) {
                            if (collectionTwo[1].contains(collectionOne[1].get(l))) {
                                Scenario scenario = ei.scenarioCorpusCoder.decode(collectionOne[1].get(l));
                                ece.obeyScenarios[1].add(scenario);
                            }
                        }
                    }
                    if (!ece.obeyScenarios[0].isEmpty() && !ece.obeyScenarios[1].isEmpty()) {
                        consistencyErrors.add(ece);
                    }
                }
            }
        }
        return consistencyErrors;
    }
}
