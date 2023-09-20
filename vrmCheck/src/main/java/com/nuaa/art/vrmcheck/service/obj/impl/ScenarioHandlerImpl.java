package com.nuaa.art.vrmcheck.service.obj.impl;

import com.nuaa.art.vrmcheck.model.ScenarioCorpusCoder;
import com.nuaa.art.vrmcheck.model.obj.EventsInformation;
import com.nuaa.art.vrmcheck.model.obj.NuclearCondition;
import com.nuaa.art.vrmcheck.model.Scenario;
import com.nuaa.art.vrmcheck.model.obj.ConditionsInformation;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScenarioHandlerImpl implements com.nuaa.art.vrmcheck.service.obj.ScenarioHandler {

    /**
     * 构建场景全集
     * 场景全集表现为一个编码解码器，每一个编码对应一个场景
     *
     * @param varNum    变量数量。
     * @param varRanges 变量值域
     * @return {@link ScenarioCorpusCoder}
     */
    @Override
    public ScenarioCorpusCoder constructScenarioCorpus(int varNum, int[] varRanges){
        return new ScenarioCorpusCoder(varNum, varRanges);
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
        ArrayList<Long> scenarioCollectionSingle = new ArrayList<Long>(); //
        for (int j = 0; j < scenarioCollection.size(); j++) {
            long code = scenarioCollection.get(j);
            Scenario thisScenario = coder.decode(code); //解码场景
            //System.out.println("oldState:" + code);
            ArrayList<Integer> inconsidered = new ArrayList<Integer>();
            for (int i = 0; i < thisScenario.scenario.length; i++) {
                if (thisScenario.scenario[i] == 0) { // 场景里 为零， 则说明没有取到离散化值域里任一区间，即缺省值
                    inconsidered.add(i);
                }
            }

            int[] inconsideredRanges = new int[inconsidered.size()];
            // todo 不太懂， 有BUG
            for (int i = 0; i < inconsidered.size(); i++) {
                inconsideredRanges[i] = coder.variableRanges[inconsidered.get(i)] - 2;
            }
            ScenarioCorpusCoder c = new ScenarioCorpusCoder(inconsidered.size(), inconsideredRanges);
            for (long l = 0; l < c.codeLimit; l++) {
                Scenario s = c.decode(l);
                long newCode = code;
                for (int i = 0; i < inconsidered.size(); i++) {
                    newCode += coder.weights[inconsidered.get(i)] * (s.scenario[i] + 1);
                }
                //System.out.println("newState:" + newCode);
                if (!scenarioCollectionSingle.contains(newCode))
                    scenarioCollectionSingle.add(newCode);
            }
        }
        return scenarioCollectionSingle;
    }

    /**
     * 构建条件的等价场景集
     * 将条件转换为场景集与行号的映射关系
     * 1.将每个合取式转换为一个或多个含0的场景
     * 2.将每行的行号填入对应的场景索引位置（相同赋值视为一行）
     *
     * @param ci {@link ConditionsInformation} 条件信息
     * @return ArrayList<HashSet<Integer>> 条件等价场景集
     */
    @Override
    public ArrayList<HashSet<Integer>> buildEquivalentScenarioSet(ConditionsInformation ci){
        ArrayList<HashSet<Integer>> equivalentScenarioSet = new ArrayList<HashSet<Integer>>();

        for (long l = 0; l < ci.scenarioCorpusCoder.codeLimit; l++) { //初始化每个场景对应的输出值号为空
            if (!ci.scenarioCorpusCoder.decode(l).containsZero())
                equivalentScenarioSet.add(new HashSet<Integer>());
            else
                equivalentScenarioSet.add(null);
        }

        if (ci.continualVariables.size() + ci.discreteVariables.size() == 0) {
            ci.equivalentScenarioSet = equivalentScenarioSet;
            return equivalentScenarioSet;
        }
        for (int i=0; i< ci.nuclearTreeForEachRow.size(); i++) {// 遍历每行的析取范式树
            ArrayList<ArrayList<NuclearCondition>> orTree = ci.nuclearTreeForEachRow.get(i);
            //System.out.println(orTree.toString());
            if (orTree.get(0).get(0).isTrue) {// 如果第一个合取式的第一个原子条件为true，则整个条件就是true
                for (Set<Integer> outputForThisState : equivalentScenarioSet) {
                    outputForThisState.add(
                            ci.outputRanges.indexOf(ci.assignmentForEachRow.get(ci.nuclearTreeForEachRow.indexOf(orTree))));
                }
                continue;
            } else if (orTree.get(0).get(0).isFalse) {// 如果第一个合取式的第一个原子条件为false，则整个条件就是false
                continue;
            }

            ArrayList<Scenario> scenarioCollection = new ArrayList<Scenario>();// 为每行创建一个等价场景集
            for (ArrayList<NuclearCondition> andTree : orTree) {// 遍历析取范式树的每个合取式
                for (Scenario thisScenario : buildAndTreeEquivalentScenarioSet(ci, andTree)) {
                    if (!scenarioCollection.contains(thisScenario))
                        scenarioCollection.add(thisScenario);
                }
            }

            for (Scenario thisScenario : scenarioCollection) {  // 将每行对应的赋值与场景集关联。
                for (long l = 0; l < ci.scenarioCorpusCoder.codeLimit; l++) {
                    Scenario s = ci.scenarioCorpusCoder.decode(l);
                    if (!s.containsZero() && s.almostEquals(thisScenario)) {
                        equivalentScenarioSet.get((int) l).add(ci.outputRanges
                                .indexOf(ci.assignmentForEachRow.get(i)));
//                        System.out.println(equivalentScenarioSet.get((int) l).size());
//                        System.out.println(i);
//                        System.out.println(ci.assignmentForEachRow.get(i));
                    }
                }
            }
        }
        ci.equivalentScenarioSet = equivalentScenarioSet;
        return equivalentScenarioSet;
    }

    /**
     * 构建事件的等价场景集序偶
     * 1.将每个AND事件的两个条件转换为场景集对
     * 2.将场景集对按照事件的逻辑语义进行集合运算，转换为时序场景集对）
     *
     * @param ei
     * @return
     */
    @Override
    public void buildEquivalentScenarioSetPair(EventsInformation ei) {
        // 解析获得每行事件中两个条件分别对应的等价场景。
        for (ArrayList<ArrayList<ArrayList<NuclearCondition>>[]> nuclearTreeForThisRow : ei.nuclearTreeForEachRow) {
            ArrayList<ArrayList<Long>[]> scenarioSetPairForThisRow = new ArrayList<ArrayList<Long>[]>();
            for (ArrayList<ArrayList<NuclearCondition>>[] nuclearTreeForThisAnd : nuclearTreeForThisRow) {
                if (ei.continualVariables.size() + ei.discreteVariables.size() != 0) {
                    ArrayList<ArrayList<Long>> scenarioSetForThis = buildAndEventTreeEquivalentScenarioSet(ei,nuclearTreeForThisAnd);
                    ArrayList<Long>[] timeStatesForThisAnd = new ArrayList[2];
                    // 进行场景分解，消除缺省变量，使得事件中所有变量都有值，这样才能与事件中其他条件比较。
                    timeStatesForThisAnd[0] = scenarioResolving(scenarioSetForThis.get(0),ei.scenarioCorpusCoder);
                    timeStatesForThisAnd[1] = scenarioResolving(scenarioSetForThis.get(1),ei.scenarioCorpusCoder);
                    scenarioSetPairForThisRow.add(timeStatesForThisAnd);
                }
            }
            ei.twoScenarioSetForEachRow.add(scenarioSetPairForThisRow);
        }

        // 按照事件谓词和时序谓词计算事件等价场景集序偶
        for (int i = 0; i < ei.twoScenarioSetForEachRow.size(); i++) {
            ArrayList<ArrayList<Long>[]> twoScenarioSetForThisRow = ei.twoScenarioSetForEachRow.get(i);
            ArrayList<ArrayList<Long>[]> scenarioSetForThisRow = new ArrayList<ArrayList<Long>[]>();
            for (int j = 0; j < twoScenarioSetForThisRow.size(); j++) {
                ArrayList<Long>[] twoScenarioSetForThisAnd = twoScenarioSetForThisRow.get(j);
                scenarioSetForThisRow.add(caculateScenarioSetPairForRow(ei.eventOperatorsForEachRow.get(i).get(j), ei.guardOperatorsForEachRow.get(i).get(j),
                        ei.scenarioCorpusCoder, twoScenarioSetForThisAnd[0], twoScenarioSetForThisAnd[1]));
            }

            ArrayList<Long>[] scenarioCollectionForThisRow = new ArrayList[2];
            scenarioCollectionForThisRow[0] = new ArrayList<Long>(scenarioSetForThisRow.get(0)[0]);
            scenarioCollectionForThisRow[1] = new ArrayList<Long>(scenarioSetForThisRow.get(0)[1]);
            for (int k = 1; k < scenarioSetForThisRow.size(); k++) {
                scenarioCollectionForThisRow[0].retainAll(scenarioSetForThisRow.get(k)[0]);
                scenarioCollectionForThisRow[1].retainAll(scenarioSetForThisRow.get(k)[1]);
            }
            ei.scenarioSetPair.add(scenarioCollectionForThisRow);
        }

    }


    public ArrayList<Scenario> buildAndTreeEquivalentScenarioSet(ConditionsInformation ci, ArrayList<NuclearCondition> andTree){
        int keyVariableSize = ci.continualVariables.size() + ci.discreteVariables.size();
        ArrayList<Scenario> thisAndTreeScenarios = new ArrayList<>();// 对于每个合取式，先新建一个临时列表存储要加入该行条件的状态集合的状态
        ArrayList<Integer>[] valuesForEachVariable = new ArrayList[keyVariableSize];
        boolean[] isSetForEachVariable = new boolean[keyVariableSize];
        for (NuclearCondition nuclear : andTree) {// 遍历合取式的每个原子条件
            ArrayList<Integer> thisVariableValues = new ArrayList<Integer>();
            int variableIndexInState;
            if (ci.continualVariables.contains(nuclear.getVar1())) {// 该原子条件为连续型变量
                int variableIndex = ci.continualVariables.indexOf(nuclear.getVar1());// 得到变量在连续变量集合里的索引值
                variableIndexInState = variableIndex;
                if (nuclear.getOperator().equals("=")) {// 等于变量对应的取值集合中第n个值，等价于变量等于离散值2n
                    thisVariableValues.add(ci.continualValues.get(variableIndex).indexOf(nuclear.getVar2()) * 2 + 2);
                } else if (nuclear.getOperator().equals("<")) {// 小于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n-1
                    for (int value = 1; value < ci.continualValues.get(variableIndex).indexOf(nuclear.getVar2())
                            * 2 + 2; value++) {
                        thisVariableValues.add(value);
                    }
                } else if (nuclear.getOperator().equals(">")) {// 大于变量对应的取值集合中第n个值，等价于变量等于离散值2n+1..max
                    for (int value = ci.continualValues.get(variableIndex).indexOf(nuclear.getVar2()) * 2
                            + 3; value < ci.continualValues.get(variableIndex).size() * 2 + 2; value++) {
                        thisVariableValues.add(value);
                    }
                } else if (nuclear.getOperator().equals("<=")) {// 小于等于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n
                    for (int value = 1; value <= ci.continualValues.get(variableIndex).indexOf(nuclear.getVar2())
                            * 2 + 2; value++) {
                        thisVariableValues.add(value);
                    }
                } else if (nuclear.getOperator().equals(">=")) {// 大于等于变量对应的取值集合中第n个值，等价于变量等于离散值2n..max
                    for (int value = ci.continualValues.get(variableIndex).indexOf(nuclear.getVar2()) * 2
                            + 2; value < ci.continualValues.get(variableIndex).size() * 2 + 2; value++) {
                        thisVariableValues.add(value);
                    }
                } else {// 不等于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n-1&2n+1..max
                    for (int value = 1; value < ci.continualValues.get(variableIndex).size() * 2
                            + 2; value++) {
                        if (value != ci.continualValues.get(variableIndex).indexOf(nuclear.getVar2()) * 2 + 2)
                            thisVariableValues.add(value);
                    }
                }
            } else {// 该原子条件为离散型变量
                int variableIndex = ci.discreteVariables.indexOf(nuclear.getVar1());// 得到变量在离散变量集合里的索引值
                variableIndexInState = variableIndex + ci.continualVariables.size();// 变量在状态中的索引值，因为连续变量和离散变量共同组成状态，所以该索引值应该在离散变量索引的基础上加上连续变量的个数
                if (nuclear.getOperator().equals("=")) {// 等于变量对应的取值集合中第n个值，等价于变量等于离散值n
                    thisVariableValues.add(
                            ci.discreteRanges.get(variableIndex).indexOf(nuclear.getVar2()) + 1);
                } else {// 不等于变量对应的取值集合中第n个值，等价于变量等于离散值1..n-1&n+1..max
                    for (int value = 1; value < ci.discreteRanges.get(variableIndex).size() + 1; value++) {
                        if (value != ci.discreteRanges.get(variableIndex).indexOf(nuclear.getVar2()) + 1)
                            thisVariableValues.add(value);
                    }
                }
            }
            if (!isSetForEachVariable[variableIndexInState]) {
                valuesForEachVariable[variableIndexInState] = thisVariableValues;
            } else {
                valuesForEachVariable[variableIndexInState].retainAll(thisVariableValues);
            }
            isSetForEachVariable[variableIndexInState] = true;
        }

        thisAndTreeScenarios.add(new Scenario(keyVariableSize));
        for (int i = 0; i < keyVariableSize; i++) {
            if (isSetForEachVariable[i]) {
                int previousCount = thisAndTreeScenarios.size();
                for (int j = 0; j < previousCount; j++) {
                    Scenario thisScenario = thisAndTreeScenarios.get(j);
                    boolean isFirstSet = false;
                    for (Integer value : valuesForEachVariable[i]) {
                        if (!isFirstSet) {
                            thisScenario.scenario[i] = value;
                            isFirstSet = true;
                            thisAndTreeScenarios.set(j, thisScenario);
                        } else {
                            Scenario newScenario = new Scenario(thisScenario);
                            newScenario.scenario[i] = value;
                            thisAndTreeScenarios.add(newScenario);
                        }
                    }
                }
            }
        }

        return thisAndTreeScenarios;
    }

    /**
     * 构建子事件树的两个等价场景集
     *
     * @param ei           事件信息
     * @param subEventTree 子事件树
     * @return {@link ArrayList}<{@link ArrayList}<{@link Long}>>
     */
    public  ArrayList<ArrayList<Long>> buildAndEventTreeEquivalentScenarioSet(EventsInformation ei, ArrayList<ArrayList<NuclearCondition>>[] subEventTree){
        ArrayList<ArrayList<Long>> scenariosCode = new ArrayList<>();
        ConditionsInformation ci = new ConditionsInformation();
        ci.nuclearTreeForEachRow.add(subEventTree[0]);
        ci.nuclearTreeForEachRow.add(subEventTree[1]);
        ci.continualVariables = ei.continualVariables;
        ci.discreteVariables = ei.discreteVariables;
        ci.continualValues = ei.continualValues;
        ci.discreteRanges = ei.discreteRanges;
        ci.continualRanges = ei.continualRanges;
        ci.scenarioCorpusCoder = ei.scenarioCorpusCoder;
        for (ArrayList<ArrayList<NuclearCondition>> orTree : ci.nuclearTreeForEachRow) {// 遍历每行的析取范式树
            ArrayList<Long> scenarioCollection = new ArrayList<Long>();// 每行的析取范式树新建一个状态集合
            for (ArrayList<NuclearCondition> andTree : orTree) {// 遍历析取范式树的每个合取式
                ArrayList<Long> thisAndTreeScenarios = new ArrayList<Long>();// 对于每个合取式，先新建一个临时列表存储要加入该行条件的状态集合的状态
                if (andTree.get(0).isTrue) { //永真式情况
                    thisAndTreeScenarios.add(0l);
                    thisAndTreeScenarios = scenarioResolving(thisAndTreeScenarios, ci.scenarioCorpusCoder); //场景分解，获取一个场景全集
                } else if (andTree.get(0).isFalse) {

                } else { //一般情况
                    ArrayList<Scenario> scenarios = buildAndTreeEquivalentScenarioSet(ci, andTree);
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

    /**
     * 计算行方案集对
     *
     * @param eventOp                    事件谓词
     * @param guardOp                    时序谓词
     * @param firstConditionScenarioSet  第一个条件的场景集
     * @param secondConditionScenarioSet 第二个条件的场景集
     * @param coder                      场景全集编解码器
     * @return {@link ArrayList}<{@link ArrayList}<{@link Long}>{@link []}>
     */
    public  ArrayList<Long>[] caculateScenarioSetPairForRow(String eventOp, String guardOp, ScenarioCorpusCoder coder,
                                        ArrayList<Long> firstConditionScenarioSet, ArrayList<Long> secondConditionScenarioSet){
        ArrayList<Long> excludeInFirstScenarioSet = new ArrayList<Long>();
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

        if (eventOp.equals("@T")) {
            if (guardOp.equals("when")) {
                for (Long scenario : excludeInFirstScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[0].add(scenario);
                }
                for (Long scenario : firstConditionScenarioSet) {
                    scenarioSetPair[1].add(scenario);
                }
            } else if (guardOp.equals("where")) {
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
            if (guardOp.equals("when")) {
                for (Long scenario : firstConditionScenarioSet) {
                    if (secondConditionScenarioSet.contains(scenario))
                        scenarioSetPair[0].add(scenario);
                }
                for (Long scenario : excludeInFirstScenarioSet) {
                    scenarioSetPair[1].add(scenario);
                }
            } else if (guardOp.equals("where")) {
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

    @Override
    public <T> ArrayList<String> getScenarioDetails(T c, Scenario s) {

        if(c instanceof ConditionsInformation ) {
            ConditionsInformation t = (ConditionsInformation) c;
            String[] concreteScenario = new String[s.variableNumber];
            for (int k = 0; k < s.variableNumber; k++) {
                if (k < t.continualVariables.size()) {
                    int value = s.scenario[k];
                    if (value == 1) {
                        concreteScenario[k] = "(" + t.continualRanges.get(k).get(0) + ","
                                + t.continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                    } else if (value == t.continualValues.get(k).size() * 2 + 1) {
                        concreteScenario[k] = "("
                                + t.continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                + t.continualRanges.get(k).get(1) + ")";
                    } else if (value % 2 == 0) {
                        concreteScenario[k] = t.continualValues.get(k).get(value / 2 - 1);
                    } else {
                        concreteScenario[k] = "("
                                + t.continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                + t.continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                    }
                } else {
                    int value = s.scenario[k];
                    concreteScenario[k] = t.discreteRanges.get(k - t.continualVariables.size())
                            .get(value - 1);
                }
            }
            return new ArrayList<>(List.of(concreteScenario));
        } else if (c instanceof EventsInformation) {
            EventsInformation t = (EventsInformation)c;
            String[] concreteScenario = new String[s.variableNumber];
            for (int k = 0; k < s.variableNumber; k++) {
                if (k < t.continualVariables.size()) {
                    int value = s.scenario[k];
                    if (value == 1) {
                        concreteScenario[k] = "(" + t.continualRanges.get(k).get(0) + ","
                                + t.continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                    } else if (value == t.continualValues.get(k).size() * 2 + 1) {
                        concreteScenario[k] = "("
                                + t.continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                + t.continualRanges.get(k).get(1) + ")";
                    } else if (value % 2 == 0) {
                        concreteScenario[k] = t.continualValues.get(k).get(value / 2 - 1);
                    } else {
                        concreteScenario[k] = "("
                                + t.continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                + t.continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                    }
                } else {
                    int value = s.scenario[k];
                    concreteScenario[k] = t.discreteRanges.get(k - t.continualVariables.size())
                            .get(value - 1);
                }
            }
            return new ArrayList<>(List.of(concreteScenario));
        } else {
            return  null;
        }

    }
}