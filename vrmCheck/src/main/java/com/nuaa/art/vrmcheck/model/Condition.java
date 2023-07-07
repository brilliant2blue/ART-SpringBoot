package com.nuaa.art.vrmcheck.model;

import com.nuaa.art.vrm.model.VRMOfXML;
import com.nuaa.art.vrmcheck.common.utils.VariableUtils;

import java.util.*;

/**
 * 条件
 *
 * @author konsin
 * {@code @date} 2023/06/21
 */
public class Condition {
    public VRMOfXML vrmModel; //所属模型
    public ArrayList<ArrayList<ArrayList<NuclearCondition>>> nuclearTreeForEachRow;// 每个条件的原子命题树
    public ArrayList<String> continualVariables;// 条件中的数值型变量
    public ArrayList<String> discreteVariables;// 条件中的枚举型变量
    public ArrayList<ContinualRange> continualRanges;
    public ArrayList<ArrayList<String>> continualValues;// 每个数值型变量在各条件中比较的值
    public ArrayList<ArrayList<String>> discreteRanges;// 每个枚举型变量的值域
    public ArrayList<String> assignmentForEachRow;// 每行的赋值
    public ArrayList<String> outputRanges;// 关联变量的值域
    public ArrayList<HashSet<Integer>> outputForEachState;// 每个场景对应的输出值号
    public int[] variableRanges;
    public Coder coder;



    // 构造方法，基于条件列表和各条件的输出值作为参数，用于条件分析
    public Condition(VRMOfXML vrmModel, ArrayList<String> conditionsForEachRow, ArrayList<String> assignmentForEachRow,
                     ArrayList<String> outputRanges) {
        this.vrmModel = vrmModel;

        getConditionInformation(conditionsForEachRow, assignmentForEachRow);
        this.coder = new Coder(this.continualVariables.size() + this.discreteVariables.size(), this.variableRanges);

        this.outputForEachState = new ArrayList<HashSet<Integer>>();
        for (long l = 0; l < coder.codeLimit; l++) { //初始化每个场景对应的输出值号为空
            if (!coder.decode(l).containsZero())
                this.outputForEachState.add(new HashSet<Integer>());
            else
                this.outputForEachState.add(null);
        }

        this.outputRanges = new ArrayList<String>();
    }

    // 构造方法2.0
    public Condition(VRMOfXML vrmModel, String[] conditionsForEachRowArray) {
        ArrayList<String> assignmentForEachRow = new ArrayList<String>();
        ArrayList<String> conditionsForEachRow = new ArrayList<String>();
        for (String condition : conditionsForEachRowArray) {
            assignmentForEachRow.add("");
            conditionsForEachRow.add(condition);
        }
        this.vrmModel = vrmModel;


        getConditionInformation(conditionsForEachRow, assignmentForEachRow);

        this.coder = new Coder(this.continualVariables.size() + this.discreteVariables.size(), this.variableRanges);

        this.outputForEachState = new ArrayList<HashSet<Integer>>();
        for (long l = 0; l < coder.codeLimit; l++) { //初始化每个场景对应的输出值号为空
            if (!coder.decode(l).containsZero())
                this.outputForEachState.add(new HashSet<Integer>());
            else
                this.outputForEachState.add(null);
        }

        this.outputRanges = new ArrayList<String>();
    }

    public Condition() {
        super();
    }

    /**
     * 构建条件的等价场景集
     * 将条件转换为场景集与行号的映射关系
     * 1.将每个合取式转换为一个或多个含0场景
     * 2.将每行的行号根据其含0场景集填入outputForEachState）
     */
    public void parseConditionIntoScenarios() {
        if (continualVariables.size() + discreteVariables.size() != 0) {
            buildScenarios();// 步骤1+2
        }
    }


    /**
     * 获取条件的基本信息
     * 1.所给条件集中每个条件的条件树， 指派值
     * 2.条件中涉及的变量， 变量的值域，连续型变量的关键值
     * 3.返回
     *
     * @param conditionsForEachRow 每行条件
     * @param assignmentForEachRow 每行的输出
     */// 根据条件列表和各条件的输出值，获取条件转换器的各种属性
    public void getConditionInformation(ArrayList<String> conditionsForEachRow, ArrayList<String> assignmentForEachRow) {

        this.assignmentForEachRow = new ArrayList<String>(assignmentForEachRow);
        nuclearTreeForEachRow = new ArrayList<ArrayList<ArrayList<NuclearCondition>>>();
        continualVariables = new ArrayList<String>();
        discreteVariables = new ArrayList<String>();
        continualValues = new ArrayList<ArrayList<String>>();
        discreteRanges = new ArrayList<ArrayList<String>>();
        continualRanges = new ArrayList<ContinualRange>();

        for (String condition : conditionsForEachRow) {
            ArrayList<ArrayList<NuclearCondition>> orTree = new ArrayList<ArrayList<NuclearCondition>>();
            String[] orConditions = condition.split("\\|\\|");// 分离“或”条件
            for (int j = 0; j < orConditions.length; j++) {// 解析“或”条件
                String orCondition = orConditions[j];
                if (orCondition.equals("()") || orCondition.equals(""))
                    orCondition = "";
                else {
                    if (orCondition.indexOf('(') == 0)
                        orCondition = orCondition.substring(1);
                    if (orCondition.lastIndexOf(')') == orCondition.length() - 1)
                        orCondition = orCondition.substring(0, orCondition.length() - 1);
                } // 剥离“或”条件的括号
                String[] andConditions = orCondition.split("&");// 分离“与”条件
                ArrayList<NuclearCondition> andTree = new ArrayList<NuclearCondition>();

                for (int k = 0; k < andConditions.length; k++) {// 解析“与”条件
                    NuclearCondition nuclear = new NuclearCondition();
                    String content = andConditions[k];// “与”条件即原子条件

                    nuclear.syntacticParser(content); // 原子条件语句解析为原子条件

                    if(!nuclear.isTrue && !nuclear.isFalse){
                        nuclear.findCriticalVariableAndKeyValues(vrmModel.inputsNode, vrmModel.tablesNode, vrmModel.stateMachinesNode,
                                continualVariables, continualRanges,continualValues, discreteVariables, discreteRanges);
                    }

                    andTree.add(nuclear);
                }
                orTree.add(andTree);
            }
            nuclearTreeForEachRow.add(orTree);
        }

        VariableUtils.sortContinualValues(continualValues); // 整理连续型值域的关键值

        // 基于关键值的值域离散化
        variableRanges = VariableUtils.rangeDiscretization(continualVariables.size(), discreteVariables.size(), continualValues, discreteRanges);
    }


    /**
     * 构建场景抽象后的场景
     */
    public void buildScenarios() {
        for (ArrayList<ArrayList<NuclearCondition>> orTree : nuclearTreeForEachRow) {// 遍历每行的析取范式树
            if (orTree.get(0).get(0).isTrue) {// 如果第一个合取式的第一个原子条件为true，则整个条件就是true
                for (Set<Integer> outputForThisState : outputForEachState) {
                    outputForThisState.add(
                            outputRanges.indexOf(assignmentForEachRow.get(nuclearTreeForEachRow.indexOf(orTree))));
                }
                continue;
            } else if (orTree.get(0).get(0).isFalse) {// 如果第一个合取式的第一个原子条件为false，则整个条件就是false
                continue;
            }
            ArrayList<Scenario> scenarioCollection = new ArrayList<Scenario>();// 每行的析取范式树新建一个状态集合
            for (ArrayList<NuclearCondition> andTree : orTree) {// 遍历析取范式树的每个合取式
                ArrayList<Scenario> thisAndTreeScenarios = new ArrayList<Scenario>();// 对于每个合取式，先新建一个临时列表存储要加入该行条件的状态集合的状态
                ArrayList<Integer>[] valuesForEachVariable = new ArrayList[continualVariables.size()
                        + discreteVariables.size()];
                boolean[] isSetForEachVariable = new boolean[continualVariables.size() + discreteVariables.size()];

                buildAndTreeScenarios(andTree,valuesForEachVariable,isSetForEachVariable); //解析and树

                thisAndTreeScenarios.add(new Scenario(continualVariables.size() + discreteVariables.size()));
                for (int i = 0; i < continualVariables.size() + discreteVariables.size(); i++) {
                    if (isSetForEachVariable[i]) {
                        int previousCount = thisAndTreeScenarios.size();
                        for (int j = 0; j < previousCount; j++) {
                            Scenario thisScenario = thisAndTreeScenarios.get(j);
                            boolean isFirstSet = false;
                            for (Integer value : valuesForEachVariable[i]) {
                                if (!isFirstSet) {
                                    thisScenario.scenario[i] = value.intValue();
                                    isFirstSet = true;
                                    thisAndTreeScenarios.set(j, thisScenario);
                                } else {
                                    Scenario newScenario = new Scenario(thisScenario);
                                    newScenario.scenario[i] = value.intValue();
                                    thisAndTreeScenarios.add(newScenario);
                                }
                            }
                        }
                    }
                }
                for (Scenario thisScenario : thisAndTreeScenarios) {
                    if (!scenarioCollection.contains(thisScenario))
                        scenarioCollection.add(thisScenario);
                }
            }
            for (Scenario thisScenario : scenarioCollection) {
                for (long l = 0; l < coder.codeLimit; l++) {
                    Scenario s = coder.decode(l);
                    if (!s.containsZero() && s.almostEquals(thisScenario))
                        outputForEachState.get((int) l).add(outputRanges
                                .indexOf(assignmentForEachRow.get(nuclearTreeForEachRow.indexOf(orTree))));
                }
            }
        }
    }

    public void buildAndTreeScenarios(ArrayList<NuclearCondition> andTree, ArrayList<Integer>[] valuesForEachVariable, boolean[] isSetForEachVariable){
        for (NuclearCondition nuclear : andTree) {// 遍历合取式的每个原子条件
            ArrayList<Integer> thisVariableValues = new ArrayList<Integer>();
            int variableIndexInState;
            if (continualVariables.contains(nuclear.variable)) {// 该原子条件为连续型变量
                int variableIndex = continualVariables.indexOf(nuclear.variable);// 得到变量在连续变量集合里的索引值
                variableIndexInState = variableIndex;
                if (nuclear.operator.equals("=")) {// 等于变量对应的取值集合中第n个值，等价于变量等于离散值2n
                    thisVariableValues.add(Integer
                            .valueOf(continualValues.get(variableIndex).indexOf(nuclear.value) * 2 + 2));
                } else if (nuclear.operator.equals("<")) {// 小于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n-1
                    for (int value = 1; value < continualValues.get(variableIndex).indexOf(nuclear.value)
                            * 2 + 2; value++) {
                        thisVariableValues.add(Integer.valueOf(value));
                    }
                } else if (nuclear.operator.equals(">")) {// 大于变量对应的取值集合中第n个值，等价于变量等于离散值2n+1..max
                    for (int value = continualValues.get(variableIndex).indexOf(nuclear.value) * 2
                            + 3; value < continualValues.get(variableIndex).size() * 2 + 2; value++) {
                        thisVariableValues.add(Integer.valueOf(value));
                    }
                } else if (nuclear.operator.equals("<=")) {// 小于等于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n
                    for (int value = 1; value <= continualValues.get(variableIndex).indexOf(nuclear.value)
                            * 2 + 2; value++) {
                        thisVariableValues.add(Integer.valueOf(value));
                    }
                } else if (nuclear.operator.equals(">=")) {// 大于等于变量对应的取值集合中第n个值，等价于变量等于离散值2n..max
                    for (int value = continualValues.get(variableIndex).indexOf(nuclear.value) * 2
                            + 2; value < continualValues.get(variableIndex).size() * 2 + 2; value++) {
                        thisVariableValues.add(Integer.valueOf(value));
                    }
                } else {// 不等于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n-1&2n+1..max
                    for (int value = 1; value < continualValues.get(variableIndex).size() * 2
                            + 2; value++) {
                        if (value != continualValues.get(variableIndex).indexOf(nuclear.value) * 2 + 2)
                            thisVariableValues.add(Integer.valueOf(value));
                    }
                }
//                for (Integer value : thisVariableValues) {
//
//                }
            } else {// 该原子条件为离散型变量
                int variableIndex = discreteVariables.indexOf(nuclear.variable);// 得到变量在离散变量集合里的索引值
                variableIndexInState = variableIndex + continualVariables.size();// 变量在状态中的索引值，因为连续变量和离散变量共同组成状态，所以该索引值应该在离散变量索引的基础上加上连续变量的个数
                if (nuclear.operator.equals("=")) {// 等于变量对应的取值集合中第n个值，等价于变量等于离散值n
                    thisVariableValues.add(
                            Integer.valueOf(discreteRanges.get(variableIndex).indexOf(nuclear.value) + 1));
                } else {// 不等于变量对应的取值集合中第n个值，等价于变量等于离散值1..n-1&n+1..max
                    for (int value = 1; value < discreteRanges.get(variableIndex).size() + 1; value++) {
                        if (value != discreteRanges.get(variableIndex).indexOf(nuclear.value) + 1)
                            thisVariableValues.add(Integer.valueOf(value));
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
    }

}

