package com.nuaa.art.vrmcheck.service.table.impl;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrmcheck.model.scenario.Scenario;
import com.nuaa.art.vrmcheck.model.scenario.ScenarioCorpusCoder;
import com.nuaa.art.vrmcheck.model.table.*;
import com.nuaa.art.vrmcheck.service.table.ScenarioHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("ScenarioV2")
public class ScenarioHandlerImplV2 implements ScenarioHandler {

    @Resource(name="ScenarioV1")
    ScenarioHandlerImpl scenarioHandler;


    /**
     * 构建条件的等价场景集
     * 将条件转换为场景集与行号的映射关系
     * 1.将每个合取式转换为一个或多个含未确定值的场景
     * 2.按每行来构建场景集（相同赋值视为一行）
     *
     * @param ci {@link AndOrConditionsInformation} 条件信息
     */
    @Override
    public void buildEquivalentScenarioSet(AndOrConditionsInformation ci) {
        ArrayList<HashSet<Long>> equivalentScenarioSet = new ArrayList<>(); // 场景编码为索引， 行的输出值为索引值. 使用哈希表是因为可以去重

        for (long l = 0; l < ci.outputRanges.size(); l++) { //初始化每行对应的场景集为空
             equivalentScenarioSet.add(new HashSet<Long>());
        }

        if (ci.criticalVariables.size() == 0) {
            ci.equivalentScenarioSet = equivalentScenarioSet;
            return;
        }
        HashSet<Integer> defaultRow = new HashSet<>();
        HashSet<Integer> trueRow = new HashSet<>();
        for (int i=0; i< ci.nuclearTreeForEachRow.size(); i++) {// 遍历每行的析取范式树
            ArrayList<ArrayList<NuclearCondition>> orTree = ci.nuclearTreeForEachRow.get(i); //获取一行条件
            //LogUtils.error(String.valueOf(orTree.get(0).get(0).isDefault));
            //System.out.println(orTree.toString());
            if (orTree.get(0).get(0).isTrue()) {// 如果第一个合取式的第一个原子条件为true，则整个条件就是true
//                for (Set<Integer> outputForThisState : equivalentScenarioSet) { //遍历场景全集，将永真式的赋值与全部场景编号进行对应。
//                    outputForThisState.add(
//                            ci.outputRanges.indexOf(ci.assignmentForEachRow.get(ci.nuclearTreeForEachRow.indexOf(orTree))));
//                }
                trueRow.add(ci.outputRanges.indexOf(ci.assignmentForEachRow.get(ci.nuclearTreeForEachRow.indexOf(orTree))));
                continue;
            } else if (orTree.get(0).get(0).isFalse()) {// 如果第一个合取式的第一个原子条件为false，则整个条件就是false
                continue;
            } else if (orTree.get(0).get(0).isDefault()){
                // 此时需要先记录默认行
                defaultRow.add(ci.outputRanges.indexOf(ci.assignmentForEachRow.get(ci.nuclearTreeForEachRow.indexOf(orTree))));
                continue;
            }

            ArrayList<Scenario> scenarioCollection = new ArrayList<Scenario>();// 为每行创建一个等价场景集
            for (ArrayList<NuclearCondition> andTree : orTree) {// 遍历析取范式树的每个合取式
                for (Scenario thisScenario : scenarioHandler.buildAndTreeEquivalentScenarioSet(ci, andTree)) {
                    if (!scenarioCollection.contains(thisScenario))
                        scenarioCollection.add(thisScenario);
                }
            }

            for (Scenario thisScenario : scenarioCollection) {  // 将每行对应的赋值与场景集关联。
//                for (long l = 0; l < ci.scenarioCorpusCoder.codeLimit; l++) {
//                    Scenario s = ci.scenarioCorpusCoder.decode(l);
//                    if (!s.containsZero() && s.almostEquals(thisScenario)) {
//                        equivalentScenarioSet.get(ci.outputRanges.indexOf(ci.assignmentForEachRow.get(i))).add((int) l);
//                    }
//                }
                equivalentScenarioSet.get(
                        ci.outputRanges.indexOf(ci.assignmentForEachRow.get(i))
                        ).add(ci.scenarioCorpusCoder.encode(thisScenario));
            }
        }
        ci.rowsForTrueScenarioSet = new ArrayList<>(trueRow);
        ci.rowsForDefaultScenarioSet = new ArrayList<>(defaultRow);
//        // 填充默认行永真行对应的等价场景
//        if(!defaultRow.isEmpty())
//            for (Set<Integer> outputForThisState : equivalentScenarioSet) { //遍历场景全集，将默认行的赋值与剩余编号进行对应。
//                if(outputForThisState.isEmpty())
//                    outputForThisState.addAll(defaultRow);
//            }
//        if(!trueRow.isEmpty())
//            for (Set<Integer> outputForThisState : equivalentScenarioSet) { //遍历场景全集，将永真式的赋值与全部场景编号进行对应。
//                outputForThisState.addAll(trueRow);
//            }
        ci.equivalentScenarioSet = equivalentScenarioSet;
    }

    /**
     * 构建事件的等价场景集序偶
     * 1.将每个AND事件的两个条件转换为场景集对
     * 2.将场景集对按照事件的逻辑语义进行集合运算，转换为时序场景集对）
     *
     * @param ei {@link AndEventsInformation} 事件信息
     * @return ArrayList<HashSet < Integer>> 事件等价场景集序偶
     */
    @Override
    public void buildEquivalentScenarioSetPair(AndEventsInformation ei) {
        scenarioHandler.buildEquivalentScenarioSetPair(ei);
    }

    /**
     * 构建事件的等价场景集序偶
     * 1.将每个AND事件的两个条件转换为场景集对
     * 2.将场景集对按照事件的逻辑语义进行集合运算，转换为时序场景集对）
     * 因为不需要对事件进行完整性分析，
     * 如果要使用场景全集对默认行和永真行进行等价场景集序偶的构建，事实上还是穷举了场景集的所有组合。
     * 生成了大量不参与分析报告内容的场景，是对空间的极大浪费。因此记录下这些特殊行，然后按其语义与收集的有效场景进行分析即可。
     *
     * @param ei {@link AndOrEventsInformation} 事件信息
     * @return ArrayList<HashSet < Integer>> 事件等价场景集序偶
     */
    @Override
    public void buildEquivalentScenarioSetPair(AndOrEventsInformation ei) {
        HashMap<Integer, ArrayList<ArrayList<Long>[]>> ScenarioSetPairsOfEachRow = new HashMap<>();
        HashSet<Integer> defaultRow = new HashSet<>();
        HashSet<Integer> trueRow = new HashSet<>();
        for(int i = 0; i<ei.outputRanges.size(); i++){
            ScenarioSetPairsOfEachRow.put(i,new ArrayList<>());
        }
        for(ArrayList<ArrayList<CoreEvent>> oneOrTree: ei.nuclearTreeForEachRow) { // 获取一行
            //System.out.println(ei.nuclearTreeForEachRow.indexOf(oneOrTree));

            Integer rowId = ei.outputRanges.indexOf(ei.assignmentForEachRow.get(ei.nuclearTreeForEachRow.indexOf(oneOrTree)));
            if (oneOrTree.get(0).get(0).isTrue) {
                trueRow.add(rowId);
            } else if (oneOrTree.get(0).get(0).isFalse()) {// 如果第一个合取式的第一个核事件为false，则整个事件就是false
            } else if (oneOrTree.get(0).get(0).isDefault()) {
                defaultRow.add(rowId);
            }
        }
        // 记录永真行，默认行
        ei.rowsForDefaultScenarioSet = new ArrayList<>(defaultRow);
        ei.rowsForTrueScenarioSet = new ArrayList<>(trueRow);


        for(ArrayList<ArrayList<CoreEvent>> oneOrTree: ei.nuclearTreeForEachRow){ // 获取一行
            Integer rowId = ei.outputRanges.indexOf(ei.assignmentForEachRow.get(ei.nuclearTreeForEachRow.indexOf(oneOrTree)));
            if(trueRow.contains(rowId)||defaultRow.contains(rowId)) continue; // 如果该行已有者true事件或default事件，则不生成场景.
            ArrayList<ArrayList<Long>[]> ScenarioSetPairOfThisTree = new ArrayList<>();
            for(ArrayList<CoreEvent> oneAndTree: oneOrTree) { //对这行的析取范式树进行分解
                ArrayList< //一个and事件内几个核事件的
                        ArrayList<Long>[] //每个核事件的两个条件对应的
                        > thisAndScenarioSets = new ArrayList<>(); //这个and事件对应的原始等价场景集集合
                for(CoreEvent coreEvent: oneAndTree){
                    if (ei.criticalVariables.size() != 0) {

                        ArrayList<ArrayList<Long>> scenarioSetForThis = buildAndEventTreeEquivalentScenarioSet(ei, coreEvent);
                        ArrayList<Long>[] timeStatesForThisAnd = new ArrayList[2];
                        // 进行场景分解，消除缺省变量，使得事件中所有变量都有值，这样才能与事件中其他条件比较。
                        timeStatesForThisAnd[0] = scenarioHandler.scenarioResolving(scenarioSetForThis.get(0),ei.scenarioCorpusCoder);
                        timeStatesForThisAnd[1] = scenarioHandler.scenarioResolving(scenarioSetForThis.get(1),ei.scenarioCorpusCoder);
                        thisAndScenarioSets.add(timeStatesForThisAnd);
                    }
                }
                // 获得合取式中每个核事件的前后等价场景S，D后, 计算该合取式的等价场景集序偶。
                // 按照事件谓词和时序谓词计算and事件等价场景集序偶
                ArrayList<ArrayList<Long>[]> scenarioSetForThisAnd = new ArrayList<ArrayList<Long>[]>();
                for (int j = 0; j < thisAndScenarioSets.size(); j++) { //对合取式中每一个and子式
                    ArrayList<Long>[] twoScenarioSetForThisCore = thisAndScenarioSets.get(j);
                    // 将两个原始场景集计算为场景集序偶 // todo 有点问题,如果处理后场景集序偶为空了，也应该是一个需要分析出来的错误
                    scenarioSetForThisAnd.add(caculateScenarioSetPairForCoreEvent( oneAndTree.get(j),
                            ei.scenarioCorpusCoder, twoScenarioSetForThisCore[0], twoScenarioSetForThisCore[1]));
                }

                    ArrayList<Long>[] scenarioCollectionForThisAnd = new ArrayList[2];
                    scenarioCollectionForThisAnd[0] = new ArrayList<Long>(scenarioSetForThisAnd.get(0)[0]);
                    scenarioCollectionForThisAnd[1] = new ArrayList<Long>(scenarioSetForThisAnd.get(0)[1]);
                    for (int k = 1; k < scenarioSetForThisAnd.size(); k++) {
                        scenarioCollectionForThisAnd[0].retainAll(scenarioSetForThisAnd.get(k)[0]); //不同的and事件中 条件对应的等价场景集取交集。
                        scenarioCollectionForThisAnd[1].retainAll(scenarioSetForThisAnd.get(k)[1]);
                    }
                ScenarioSetPairOfThisTree.add(scenarioCollectionForThisAnd); //将该合取式的等价场景集序偶添加到析取式的等价场景集序偶中
            }
            // 并入该行对应的场景集序偶列表中。
//            if(!ScenarioSetPairsOfEachRow.containsKey(rowId)){
//                ScenarioSetPairsOfEachRow.put(rowId, ScenarioSetPairOfThisTree);
//            } else {
                ScenarioSetPairsOfEachRow.get(rowId).addAll(ScenarioSetPairOfThisTree);
//            }

        }
        ei.scenarioSetPairsOfEachRow = ScenarioSetPairsOfEachRow;
//        for(var t : ei.scenarioSetPairsOfEachRow.values()){
//            System.out.println("场景");
//            for(var i: t){
//                Arrays.stream(i).forEach(it->System.out.print(it.toString()));
//                System.out.println();
//            }
//        }
    }

    /**
     * 构建场景全集
     * 场景全集表现为一个编码解码器，每一个编码对应一个场景
     *
     * @param varNum    变量数量。
     * @param varRanges 变量值域
     * @return {@link ScenarioCorpusCoder}
     */
    @Override
    public ScenarioCorpusCoder constructScenarioCorpus(int varNum, int[] varRanges) {
        return scenarioHandler.constructScenarioCorpus(varNum, varRanges);
    }

    /**
     * 场景分解
     * 确定单条条件场景中的未确定取值，也可用来构建场景全集
     * 前提是，创建场景时变量集要比较完整，即场景分解的变量要全部在内，只是未确定取值。
     * 场景分解的作用则是将这组变量未确定取值的场景分解为一组取到所有值的组合的场景
     *
     * @param scenarioCollection 待分解的场景集合
     * @param coder              场景全集
     * @return {@link ArrayList}<{@link Long}>
     */
    @Override
    public ArrayList<Long> scenarioResolving(ArrayList<Long> scenarioCollection, ScenarioCorpusCoder coder) {
        return scenarioHandler.scenarioResolving(scenarioCollection, coder);
    }

    @Override
    public ArrayList<String> getScenarioDetails(CriticalVariables c, Scenario s) {
        return scenarioHandler.getScenarioDetails(c, s);
    }



    /**
     * 构建核心事件的两个等价场景集
     * 注意：： 在事件中，两个子条件不能为default类型。
     * @param ei           事件信息
     * @param coreEvent 子事件树
     * @return {@link ArrayList}<{@link ArrayList}<{@link Long}>>
     */
    public  ArrayList<ArrayList<Long>> buildAndEventTreeEquivalentScenarioSet(AndOrEventsInformation ei, CoreEvent coreEvent){
        ArrayList<ArrayList<Long>> scenariosCode = new ArrayList<>();
        AndOrConditionsInformation ci = new AndOrConditionsInformation();
        ci.nuclearTreeForEachRow.add(coreEvent.eventCondition);
        ci.nuclearTreeForEachRow.add(coreEvent.guardCondition);
        ci.criticalVariables = ei.criticalVariables;
        ci.scenarioCorpusCoder = ei.scenarioCorpusCoder;
        for (ArrayList<ArrayList<NuclearCondition>> orTree : ci.nuclearTreeForEachRow) {// 遍历每行的析取范式树
            ArrayList<Long> scenarioCollection = new ArrayList<Long>();// 每行的析取范式树新建一个状态集合
            for (ArrayList<NuclearCondition> andTree : orTree) {// 遍历析取范式树的每个合取式
                ArrayList<Long> thisAndTreeScenarios = new ArrayList<Long>();// 对于每个合取式，先新建一个临时列表存储要加入该行条件的状态集合的状态
                if (andTree.get(0).isTrue) { //永真式情况
                    thisAndTreeScenarios.add(0L);
                    thisAndTreeScenarios = scenarioHandler.scenarioResolving(thisAndTreeScenarios, ci.scenarioCorpusCoder); //场景分解，获取一个场景全集
                } else if (andTree.get(0).isFalse) {

                } else { //一般情况
                    ArrayList<Scenario> scenarios = scenarioHandler.buildAndTreeEquivalentScenarioSet(ci, andTree);
                    //thisAndTreeScenarios.add(0l);
                    for(Scenario scenario: scenarios){
                        long code = ci.scenarioCorpusCoder.encode(scenario);
                        if(!thisAndTreeScenarios.contains(code)){
                            thisAndTreeScenarios.add(code);
                        }
                    }

                }
                for (Long thisState : thisAndTreeScenarios) {
                    if (!scenarioCollection.contains(thisState))
                        scenarioCollection.add(thisState);
                }
            }
            scenariosCode.add(scenarioCollection);
        }
        return scenariosCode;
    }

    ArrayList<Long>[] caculateScenarioSetPairForCoreEvent(CoreEvent coreEvent, ScenarioCorpusCoder coder,
                            ArrayList<Long> firstConditionScenarioSet, ArrayList<Long> secondConditionScenarioSet){
        ArrayList<Long> excludeInFirstScenarioSet = new ArrayList<Long>(); //计算事件谓词条件的补集
        for (long l = 0; l < coder.codeLimit; l++) {
            boolean isZeroIn = false;
            Scenario thisScenario = coder.decode(l);
            for (int k = 0; k < thisScenario.variableNumber; k++) {
                if (thisScenario.scenario[k] == 0) {
                    isZeroIn = true;
                    break;
                }
            }
            if (isZeroIn) {
                continue;
            }
            if (!firstConditionScenarioSet.contains(l))
                excludeInFirstScenarioSet.add(l);
        }

        ArrayList<Long>[] scenarioSetPair = new ArrayList[2];
        scenarioSetPair[1] = new ArrayList<>();
        scenarioSetPair[0] = new ArrayList<>();

        if (coreEvent.eventOperator.equals("@T")) {
            if (coreEvent.guardOperator.equalsIgnoreCase("when")) {
                for (Long scenario : excludeInFirstScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[0].add(scenario);
                }
                for (Long scenario : firstConditionScenarioSet) {
                    scenarioSetPair[1].add(scenario);
                }
            } else if (coreEvent.guardOperator.equalsIgnoreCase("where")) {
                for (Long scenario : excludeInFirstScenarioSet) {
                    scenarioSetPair[0].add(scenario);
                }
                for (Long scenario : firstConditionScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[1].add(scenario);
                }
            } else {
                for (Long scenario : excludeInFirstScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[0].add(scenario);
                }
                for (Long scenario : firstConditionScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[1].add(scenario);
                }
            }
        } else {
            if (coreEvent.guardOperator.equalsIgnoreCase("when")) {
                for (Long scenario : firstConditionScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[0].add(scenario);
                }
                for (Long scenario : excludeInFirstScenarioSet) {
                    scenarioSetPair[1].add(scenario);
                }
            } else if (coreEvent.guardOperator.equalsIgnoreCase("where")) {
                for (Long scenario : firstConditionScenarioSet) {
                    scenarioSetPair[0].add(scenario);
                }
                for (Long scenario : excludeInFirstScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[1].add(scenario);
                }
            } else {
                for (Long scenario : firstConditionScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[0].add(scenario);
                }
                for (Long scenario : excludeInFirstScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[1].add(scenario);
                }
            }
        }
        return scenarioSetPair;

    }

}
