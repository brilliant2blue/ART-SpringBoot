package com.nuaa.art.vrmcheck.service.table.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.common.ConceptItemType;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrmcheck.common.CheckErrorType;
import com.nuaa.art.vrmcheck.common.utils.OutputUtils;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorInfo;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;
import com.nuaa.art.vrmcheck.model.scenario.Scenario;
import com.nuaa.art.vrmcheck.model.table.AndOrConditionsInformation;
import com.nuaa.art.vrmcheck.service.table.ConditionCheck;
import com.nuaa.art.vrmcheck.service.table.ScenarioHandler;
import jakarta.annotation.Resource;
import org.apache.juli.logging.Log;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("ConditionCheckV2")
public class AndOrConditionCheckImplV2 implements ConditionCheck {

    @Resource
    AndOrConditionParserImpl conditionParser;
    @Resource(name = "ScenarioV2")
    ScenarioHandler scenarioHandler;

    @Override
    public boolean checkConditionIntegrityAndConsistency(VRM vrmModel, CheckErrorReporter errorReporter) {
        for (TableOfVRM condition : vrmModel.getConditions()) {

            // 获取在表中出现的所有源模式
            List<String> modes = condition.getRows().stream().map(TableRow::getMode).distinct().toList();

            for (String sourceMode : modes) {// 对同模式的条件进行一致性完整性判断

                List<TableRow> subTable = new ArrayList<>();
                ArrayList<Integer> wrongRowReqID = new ArrayList<Integer>();// 对应每行的需求编号
                for (TableRow row : condition.getRows()) {
                    if (row.getMode().equals(sourceMode)) {
                        subTable.add(row);
                        wrongRowReqID.add(row.getRelateReq());
                    }
                }

                //解析条件信息
                AndOrConditionsInformation ci = conditionParser.emptyInformationFactory();
                conditionParser.setParentRangeAndValueOfEachRow(vrmModel, condition, ci);
                conditionParser.praserInformationInCondtions(vrmModel, subTable, ci);
                // 生成场景编码器， 并自动存储
                ci.scenarioCorpusCoder = scenarioHandler.constructScenarioCorpus(ci.criticalVariables.size(), ci.variableRanges);
                // 生成等价场景集， 并自动存储
                scenarioHandler.buildEquivalentScenarioSet(ci);

                LogUtils.info("一致性分析中...");
                checkConditionConsistency(vrmModel.getSystem().getSystemName(),condition,ci,sourceMode,wrongRowReqID,errorReporter);
                LogUtils.info("完整性分析中...");
                checkConditionIntegrity(vrmModel.getSystem().getSystemName(),condition,ci,sourceMode,wrongRowReqID,errorReporter);
            }
        }
        return errorReporter.isConditionRight();
    }

    void checkConditionIntegrity(String modelname, TableOfVRM original, AndOrConditionsInformation ci, String sourceMode, ArrayList<Integer> wrongRowReqID, CheckErrorReporter reporter) {
        if (ci.rowsForTrueScenarioSet.size() + ci.rowsForDefaultScenarioSet.size() >= 1) {
            // 有永真式或默认式，肯定覆盖所有场景
            LogUtils.debug(original.getRelateVar().getConceptName());
            return ;
        }
        if (ci.criticalVariables.size() > 0) {
            HashSet<Scenario> scenarios = new HashSet<>();
            for(HashSet<Long> scenarioSet : ci.equivalentScenarioSet){
                for(Long code : scenarioSet){
                    scenarios.add(ci.scenarioCorpusCoder.decode(code));
                }
            }
            ArrayList<Scenario> obeyScenariosOfIntegrity = new ArrayList<>();
            int cnt = 0;
            Scenario pre = null;
            for(int i = 1; i<ci.scenarioCorpusCoder.codeLimit && cnt < 10;i++){
                //LogUtils.warn(String.valueOf(i));
                Scenario one = ci.scenarioCorpusCoder.decode(i);
                boolean flag = false;
                for(Scenario in : scenarios){
                    if(in.almostEquals(one)){
                        flag = true;
                    }
                }
                if(!flag){
                    if(pre == null || !pre.almostEquals(one)){
                        cnt++;
                        obeyScenariosOfIntegrity.add(one);
                        pre = one;
                    }
                }
            }

            if(!obeyScenariosOfIntegrity.isEmpty()) {
                String variableSet = OutputUtils.getVariableSetHeader(ci.criticalVariables.continualVariables, ci.criticalVariables.discreteVariables);

                ConditionErrorRefresh(reporter);
                String outputString = "";// 输出文本
                outputString = "错误定位：表格" + original.getName() + "\n系统："+ modelname + "\n错误内容：";
                if (!(sourceMode.isBlank() || sourceMode.equals("null")))
                    outputString += "处于模式" + sourceMode + "下时，\n";
                outputString += "当变量取值为下表任意一行的组合时";
                if (original.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
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
                reporter.addErrorList(new CheckErrorInfo(
                        reporter.getErrorCount(),
                        CheckErrorType.ConditionIntegrityValue,
                        original.getName(),
                        wrongRowReqID, original.getRelateVar().getConceptDependencyModeClass(), outputString));
            }
        } else {
            if (ci.rowsForTrueScenarioSet.size() + ci.rowsForDefaultScenarioSet.size() < 1){
                ConditionErrorRefresh(reporter);
                String outputString = "";// 输出文本
                outputString = "错误定位：表格" + original.getName() + "\n系统："+ modelname  + "\n错误内容：";
                if (!(sourceMode.isBlank() || sourceMode.equals("null")))
                    outputString += "处于模式" + sourceMode + "下时，\n";
                outputString += "表格中只存在永假条件";
                reporter.addErrorList(new CheckErrorInfo(
                        reporter.getErrorCount(),
                        CheckErrorType.ConditionIntegrityOnFalse,
                        original.getName(),
                        wrongRowReqID, original.getRelateVar().getConceptDependencyModeClass(), outputString));
            }
        }
    }

    void checkConditionConsistency(String modelname, TableOfVRM original, AndOrConditionsInformation ci, String sourceMode, ArrayList<Integer> wrongRowReqID, CheckErrorReporter reporter) {
        if (ci.rowsForTrueScenarioSet.size() + ci.rowsForDefaultScenarioSet.size() > 1) {
            // 多个永真式或默认式错误
            ConditionErrorRefresh(reporter);
            String outputString = "";// 输出文本
            outputString = "错误定位：表格" + original.getName() + "\n系统："+ modelname  + "\n错误内容：";
            if (!(sourceMode.isBlank() || sourceMode.equals("null")))
                outputString += "处于模式" + sourceMode + "下时，\n";
            outputString += "表格中有多个输出值冲突的永真条件或默认条件";
            reporter.addErrorList(new CheckErrorInfo(
                    reporter.getErrorCount(),
                    CheckErrorType.ConditionIntegrityOnFalse,
                    original.getName(),
                    wrongRowReqID, original.getRelateVar().getConceptDependencyModeClass(), outputString));
            return;
        }


        if (ci.criticalVariables.size() > 0) {
            int len = ci.outputRanges.size();
            HashMap<int[], ArrayList<Scenario>> obeyScenariosOfConsistency = new HashMap<>();
            for (int i = 0; i < len - 1; i++) {
                // 不全是永真式的两行
                ArrayList<Long> setOfLineI = new ArrayList<>(ci.equivalentScenarioSet.get(i));
                if(setOfLineI.size() > 0 || ci.rowsForTrueScenarioSet.contains(i)) {
                    //LogUtils.error(ci.outputRanges.get(i));
                    for (int j = i + 1; j < len; j++) {
                        int[] key = {i, j};
                        ArrayList<Long> setOfLineJ = new ArrayList<>(ci.equivalentScenarioSet.get(j));
                        if (setOfLineJ.size() > 0) {
                            if (ci.rowsForTrueScenarioSet.contains(i)) { // 永真式与其他表达式冲突, 则直接复制冲突场景
                                ArrayList<Scenario> scj = new ArrayList<>(setOfLineJ.size());
                                for (Long l : setOfLineJ) {
                                    scj.add(ci.scenarioCorpusCoder.decode(l));
                                }
                                obeyScenariosOfConsistency.put(key, scj);
                                continue;
                            }
                            // 遍历每组场景进行匹配
                            for (int si = 0; si < setOfLineI.size(); si++) {
                                Scenario sci = ci.scenarioCorpusCoder.decode(setOfLineI.get(si));
                                for (int sj = 0; sj < setOfLineJ.size(); sj++) {
                                    if (obeyScenariosOfConsistency.containsKey(key)
                                            &&(!(obeyScenariosOfConsistency.get(key).size() < 10))) { //反例足够多了，跳出循环
                                        si = setOfLineI.size();
                                        sj = setOfLineJ.size();
                                        break;
                                    }
                                    Scenario scj = ci.scenarioCorpusCoder.decode(setOfLineJ.get(sj));
                                    if(sci.almostEquals(scj)) {
                                        Scenario res = sci.merge(scj);
                                        if (res != null) {
                                            if (obeyScenariosOfConsistency.containsKey(key)) {
                                                if (!obeyScenariosOfConsistency.get(key).contains(res))
                                                    obeyScenariosOfConsistency.get(key).add(res);
                                            } else {
                                                obeyScenariosOfConsistency.put(key, new ArrayList<>());
                                                obeyScenariosOfConsistency.get(key).add(res);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Iterator<Map.Entry<int[], ArrayList<Scenario>>> it = obeyScenariosOfConsistency.entrySet().iterator();
            String variableSet = OutputUtils.getVariableSetHeader(ci.criticalVariables.continualVariables, ci.criticalVariables.discreteVariables);
            while ((it.hasNext())){
                Map.Entry<int[], ArrayList<Scenario>> obey = it.next();
                ArrayList<Scenario> obeyScenarios = obey.getValue();
                ConditionErrorRefresh(reporter);

                String outputString = "";// 输出文本
                outputString = "错误定位：表格" + original.getName()  + "\n系统："+ modelname
                        + "\n错误内容：";
                if (!(sourceMode.isBlank() || sourceMode.equals("null")))
                    outputString += "处于模式" + sourceMode + "下时，\n";
                outputString += "当变量取值为下表任意一行的组合时";
                if (original.getRelateVar().getConceptType().equals(ConceptItemType.Output.getNameEN()))
                    outputString += "输出变量";
                else
                    outputString += "中间变量";
                outputString += "同时取每行后方的多个不同赋值" + "\n" + variableSet;
                for (Scenario s : obeyScenarios) {
                    outputString += "|";
                    for (String value : scenarioHandler.getScenarioDetails(ci.criticalVariables, s)) {
                        outputString += String.format("%-15s", value) + "|";
                    }
                    outputString += "--->";

                    outputString += ci.outputRanges.get(obey.getKey()[0]) + "," + ci.outputRanges.get(obey.getKey()[1]);

                    outputString += "\n";
                }
                outputString = outputString.substring(0,
                                                      outputString.length() - 1);
                reporter.addErrorList(new CheckErrorInfo(
                        reporter.getErrorCount(),
                        CheckErrorType.ConditionConsistencyValue,
                        original.getName(),
                        wrongRowReqID, sourceMode, outputString));
            }

        }
    }

    void ConditionErrorRefresh(CheckErrorReporter errorReporter) {
        errorReporter.setConditionRight(false);
        errorReporter.addErrorCount();
    }
}
