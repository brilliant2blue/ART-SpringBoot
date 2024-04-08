package com.nuaa.art.vrmcheck.service.table.impl;

import com.nuaa.art.vrm.common.ConceptItemType;
import com.nuaa.art.vrm.common.utils.DataTypeUtils;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.common.utils.OutputUtils;
import com.nuaa.art.vrmcheck.model.scenario.Scenario;
import com.nuaa.art.vrmcheck.model.table.AndOrConditionsInformation;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;
import com.nuaa.art.vrmcheck.service.table.ConditionCheck;
import com.nuaa.art.vrmcheck.service.table.ScenarioHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("ConditionCheckV1")
public class AndOrConditionCheckImpl implements ConditionCheck {
    @Resource
    DataTypeUtils typeUtils;

    @Resource
    AndOrConditionParserImpl conditionPraser;
    @Resource(name = "ScenarioV1")
    ScenarioHandler scenarioHandler;

    @Override
    public boolean checkConditionIntegrityAndConsistency(VRM vrmModel, CheckErrorReporter errorReporter) {
        for (TableOfVRM condition: vrmModel.getConditions()) {

            // 获取在表中出现的所有源模式
            List<String> modes = condition.getRows().stream().map(TableRow::getMode).distinct().collect(Collectors.toList());

            for (String sourceMode: modes) {// 对同模式的条件进行一致性完整性判断

                List<TableRow> subTable = new ArrayList<>();
                ArrayList<Integer> wrongRowReqID = new ArrayList<Integer>();// 对应每行的需求编号
                for(TableRow row: condition.getRows()){
                    if (row.getMode().equals(sourceMode)) {
                        subTable.add(row);
                        wrongRowReqID.add(row.getRelateReq());
                    }
                }

                //解析条件信息
                AndOrConditionsInformation ci = conditionPraser.emptyInformationFactory();
                conditionPraser.setParentRangeAndValueOfEachRow(vrmModel, condition, ci);
                conditionPraser.praserInformationInCondtions(vrmModel, subTable, ci);
                // 生成场景编码器， 并自动存储
                ci.scenarioCorpusCoder = scenarioHandler.constructScenarioCorpus(ci.criticalVariables.size(), ci.variableRanges);
                // 生成等价场景集， 并自动存储
                scenarioHandler.buildEquivalentScenarioSet(ci);

                // 对生成的等价场景集进行条件一致性完整性检查
                ArrayList<Scenario> obeyScenariosOfConsistency = new ArrayList<>(); //违反一致性的场景
                ArrayList<Scenario> obeyScenariosOfIntegrity = new ArrayList<>(); //违反完整性的场景

                ArrayList<ArrayList<String>> obeyAssignmentOfConsistency = new ArrayList<>(); //违反一致性的赋值
                boolean isObeyConsistency = false;
                boolean isObeyIntegrity = false;
                int tureConditionNum = 0;
                if (ci.criticalVariables.size() != 0) {
                    for (int i = 0; i < ci.scenarioCorpusCoder.codeLimit; i++) {
                        Scenario s = ci.scenarioCorpusCoder.decode(i); // 从场景全集，获取一个场景
                        if (!s.containsZero() && ci.equivalentScenarioSet.get(i).size() > 1) { //如果场景不为空，且场景对应不只一行， 则发生条件一致性错误
                            obeyScenariosOfConsistency.add(s);
                            ArrayList<String> assign = new ArrayList<>();
                            for (Integer line : ci.equivalentScenarioSet.get(i))
                               assign.add(ci.outputRanges.get(line));
                            obeyAssignmentOfConsistency.add(assign);
                        }
                        if (!s.containsZero() && ci.equivalentScenarioSet.get(i).size() == 0) { //如果场景不为空，且场景不对应任何行， 则发生条件完整性错误
                            obeyScenariosOfIntegrity.add(s);
                        }
                    }
                    isObeyConsistency = !obeyScenariosOfConsistency.isEmpty();
                    isObeyIntegrity = !obeyScenariosOfIntegrity.isEmpty();
                } else { //判断同源但是不同输出的多个条件中是否具有多个永真式（如所有条件中均不涉及变量取值情况判断的话），没有限定条件，则default退化为True
                    for (int i = 0; i < ci.nuclearTreeForEachRow.size(); i++) {
                        if (ci.nuclearTreeForEachRow.get(i).get(0).get(0).isTrue || ci.nuclearTreeForEachRow.get(i).get(0).get(0).isDefault)
                            tureConditionNum++;
                    }
                    isObeyConsistency = tureConditionNum > 1;
                    isObeyIntegrity = tureConditionNum == 0; //其实就是只有True 和 False的情况下，如果没有True，那么就全是False了，当然不满足完整性
                }


                String variableSet = OutputUtils.getVariableSetHeader(ci.criticalVariables.continualVariables, ci.criticalVariables.discreteVariables);
                // 打印完整性错误信息
                if(isObeyIntegrity){
                    if(obeyScenariosOfIntegrity.size() != 0){
                        ConditionErrorRefresh(errorReporter);
                        String outputString = "";// 输出文本
                        outputString = "错误定位：表格" + condition.getName() + "\n错误内容：";
                        if (!(sourceMode.isBlank() || sourceMode.equals("null")))
                            outputString += "处于模式" + sourceMode + "下时，\n";
                        outputString += "当变量取值为下表任意一行的组合时";
                        if (condition.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
                            outputString += "输出变量";
                        else
                            outputString += "中间变量";
                        outputString += "无值\n" + variableSet;
                        for (Scenario s : obeyScenariosOfIntegrity) {
                            outputString += "|";
                            for (String value : scenarioHandler.getScenarioDetails(ci.criticalVariables, s)) {
                                outputString += String.format("%-15s", value) + "|";
                            }
                            outputString += "\n";
                        }
                        outputString = outputString.substring(0, outputString.length() - 1);
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.ConditionIntegrityValue,
                                condition.getName(),
                                wrongRowReqID, condition.getRelateVar().getConceptDependencyModeClass(), outputString));
                    } else {
                        ConditionErrorRefresh(errorReporter);
                        String outputString = "";// 输出文本
                        outputString = "错误定位：表格" + condition.getName() + "\n错误内容：";
                        if (!(sourceMode.isBlank() || sourceMode.equals("null")))
                            outputString += "处于模式" + sourceMode + "下时，\n";
                        outputString += "表格中只存在永假条件";
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.ConditionIntegrityOnFalse,
                                condition.getName(),
                                wrongRowReqID, condition.getRelateVar().getConceptDependencyModeClass(), outputString));
                    }
                }
                if(isObeyConsistency){
                    if(tureConditionNum > 1){
                        ConditionErrorRefresh(errorReporter);
                        String outputString = "";// 输出文本
                        outputString = "错误定位：表格" + condition.getName()
                                + "\n错误内容：";
                        if (!(sourceMode.isBlank() || sourceMode.equals("null")))
                            outputString += "处于模式" + sourceMode + "下时，\n";
                        outputString += "表格中有多个输出值冲突的永真条件";
                        errorReporter.addErrorList(new CheckErrorInfo(
                                errorReporter.getErrorCount(),
                                CheckErrorType.ConditionConsistencyTrue,
                                condition.getName(),
                                wrongRowReqID, condition.getRelateVar().getConceptDependencyModeClass(), outputString));
                    } else {
                        ConditionErrorRefresh(errorReporter);

                        String outputString = "";// 输出文本
                        outputString = "错误定位：表格" + condition.getName()
                                + "\n错误内容：";
                        if (!(sourceMode.isBlank() || sourceMode.equals("null")))
                            outputString += "处于模式" + sourceMode + "下时，\n";
                        outputString += "当变量取值为下表任意一行的组合时";
                        if (condition.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
                            outputString += "输出变量";
                        else
                            outputString += "中间变量";
                        outputString += "同时取每行后方的多个不同赋值" + "\n" + variableSet;
                        for (int i = 0; i < obeyScenariosOfConsistency.size(); i++) {
                            Scenario s = obeyScenariosOfConsistency.get(i);
                            outputString += "|";
                            for (String value : scenarioHandler.getScenarioDetails(ci.criticalVariables, s)) {
                                outputString += String.format("%-15s", value) + "|";
                            }
                            outputString += "--->";
                            for (String output : obeyAssignmentOfConsistency.get(i)) {
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
                                condition.getName(),
                                wrongRowReqID, sourceMode, outputString));
                    }
                }
            }
        }
        return errorReporter.isConditionRight();
    }

    void ConditionErrorRefresh(CheckErrorReporter errorReporter) {
        errorReporter.setConditionRight(false);
        errorReporter.addErrorCount();
    }
}
