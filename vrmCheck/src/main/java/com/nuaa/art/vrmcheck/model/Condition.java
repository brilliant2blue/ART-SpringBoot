package com.nuaa.art.vrmcheck.model;

import com.nuaa.art.vrm.model.model.VRMOfXML;
import org.dom4j.Element;

import java.util.*;

import static com.nuaa.art.vrmcheck.common.utils.SortContinualValues.sortContinualValues;

/**
 * 条件
 *
 * @author konsin
 * @date 2023/06/21
 */
public class Condition {
    public VRMOfXML vrmModel; //所属模型
    public ArrayList<ArrayList<ArrayList<NuclearCondition>>> nuclearTreeForEachRow;// 每个条件的原子命题树
    public ArrayList<String> continualVariables;// 条件中的数值型变量
    public ArrayList<String> discreteVariables;// 条件中的枚举型变量
    public ArrayList<ContinualRange> continualRanges;
    public ArrayList<ArrayList<String>> continualValues;// 每个数值型变量在各条件中比较的值
    public ArrayList<ArrayList<String>> discreteRanges;// 每个枚举型变量的值域
    public ArrayList<String> behaviorForEachRow;// 每行的赋值
    public ArrayList<String> outputRanges;// 关联变量的值域
    public ArrayList<HashSet<Integer>> outputForEachState;// 每个场景对应的输出值号
    public int[] variableRanges;
    public Coder coder;



    // 构造方法，基于条件列表和各条件的输出值作为参数，用于条件分析
    public Condition(VRMOfXML vrmModel, ArrayList<String> conditionsForEachRow, ArrayList<String> assignmentForEachRow,
                     ArrayList<String> outputRanges) {
        this.vrmModel = vrmModel;
        getConditionTableInformation(conditionsForEachRow, assignmentForEachRow, outputRanges);
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
        getConditionTableInformation(conditionsForEachRow, assignmentForEachRow, new ArrayList<String>());
    }

    public Condition() {
        super();
    }

    // 将条件转换为场景集与行号的映射关系（1.将每个合取式转换为一个或多个含0场景 2.将每行的行号根据其含0场景集填入outputForEachState）
    public void parseConditionIntoStates() {
        if (continualVariables.size() + discreteVariables.size() != 0) {
            buildStates();// 步骤1+2
        }
    }


    // 生成违反范式的场景集合的表头（变量的列表），提供给分析的输出信息
    public String getVariableSet() {
        int maximum = 0;
        for (String continual : continualVariables) {
            if (continual.length() / 15 > maximum)
                maximum = continual.length() / 15;
        }
        for (String discrete : discreteVariables) {
            if (discrete.length() / 15 > maximum)
                maximum = discrete.length() / 15;
        }
        String variableSet = "";
        for (int i = 0; i <= maximum; i++) {
            variableSet += "|";
            for (String continual : continualVariables) {
                if (continual.length() > 15 + i * 15)
                    variableSet += String.format("%-15s", continual.substring(0 + i * 15, 15 + i * 15)) + "|";
                else if (continual.length() > 0 + i * 15)
                    variableSet += String.format("%-15s", continual.substring(0 + i * 15)) + "|";
                else
                    variableSet += String.format("%-15s", "") + "|";
            }
            for (String discrete : discreteVariables) {
                if (discrete.length() > 15 + i * 15)
                    variableSet += String.format("%-15s", discrete.substring(0 + i * 15, 15 + i * 15)) + "|";
                else if (discrete.length() > 0 + i * 15)
                    variableSet += String.format("%-15s", discrete.substring(0 + i * 15)) + "|";
                else
                    variableSet += String.format("%-15s", "") + "|";
            }
            variableSet += "\n";
        }
        variableSet += "|";
        for (int i = 0; i < continualVariables.size() + discreteVariables.size(); i++) {
            variableSet += "---------------|";
        }
        variableSet += "\n";
        return variableSet;
    }

    // 根据条件列表和各条件的输出值，获取条件转换器的各种属性
    public void getConditionTableInformation(ArrayList<String> conditionsForEachRow,
                                             ArrayList<String> assignmentForEachRow, ArrayList<String> outputRanges) {
        behaviorForEachRow = new ArrayList<String>(assignmentForEachRow);
        nuclearTreeForEachRow = new ArrayList<ArrayList<ArrayList<NuclearCondition>>>();
        continualVariables = new ArrayList<String>();
        discreteVariables = new ArrayList<String>();
        continualValues = new ArrayList<ArrayList<String>>();
        discreteRanges = new ArrayList<ArrayList<String>>();
        continualRanges = new ArrayList<ContinualRange>();
        outputForEachState = new ArrayList<HashSet<Integer>>();
        this.outputRanges = outputRanges;
        //TODO: 将解析条件事件生成coder的部分提取出来。
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
                    if (content.equals("()") || content.equals(""))
                        content = "";
                    else {
                        if (content.indexOf('(') == 0)
                            content = content.substring(1);
                        if (content.lastIndexOf(')') == content.length() - 1)
                            content = content.substring(0, content.length() - 1);
                    } // 剥离原子条件的括号
                    if (content.contains("="))
                        nuclear.operator = "=";
                    else if (content.contains("<"))
                        nuclear.operator = "<";
                    else if (content.contains(">"))
                        nuclear.operator = ">";
                    else if (content.equalsIgnoreCase("true"))
                        nuclear.isTrue = true;
                    else
                        nuclear.isFalse = true;
                    if (!nuclear.isTrue && !nuclear.isFalse) {// 忽略true或false条件，因为其中无变量
                        nuclear.variable = content.substring(0, content.indexOf(nuclear.operator));// 该原子条件变量名
                        nuclear.value = content.substring(content.indexOf(nuclear.operator) + 1);
                        if (nuclear.variable.charAt(0) == '!') {
                            nuclear.variable = nuclear.variable.substring(1);
                            if (nuclear.operator.equals("<"))
                                nuclear.operator = ">=";
                            else if (nuclear.operator.equals(">"))
                                nuclear.operator = "<=";
                            else
                                nuclear.operator = "!=";
                        }
                        Iterator inputIterator_T = vrmModel.inputsNode.elementIterator();
                        Iterator tableIterator_T = vrmModel.tablesNode.elementIterator();
                        Iterator stateMachineIterator_T = vrmModel.stateMachinesNode.elementIterator();
                        while (inputIterator_T.hasNext()) {// 判断变量名是否为输入变量
                            Element input_T = (Element) inputIterator_T.next();
                            String name = input_T.attributeValue("name");
                            if (nuclear.variable.equals(name)) {// 变量名是输入变量
                                if (input_T.element("range").getText().contains("..")) {// 变量连续
                                    if (continualVariables.contains(nuclear.variable)) {// 变量已存储
                                        if (!continualValues.get(continualVariables.indexOf(nuclear.variable))
                                                .contains(nuclear.value)) {// 值未存储
                                            ArrayList<String> thisContinualValues = continualValues
                                                    .get(continualVariables.indexOf(nuclear.variable));
                                            thisContinualValues.add(nuclear.value);
                                            continualValues.set(continualVariables.indexOf(nuclear.variable),
                                                    thisContinualValues);
                                        }
                                    } else {// 变量未存储
                                        continualVariables.add(nuclear.variable);
                                        ContinualRange cr = new ContinualRange();
                                        String[] limits = input_T.element("range").getText().split("\\.\\.");
                                        cr.lowLimit = limits[0];
                                        cr.highLimit = limits[1];
                                        ArrayList<String> thisContinualValues = new ArrayList<String>();
                                        thisContinualValues.add(nuclear.value);
                                        continualRanges.add(cr);
                                        continualValues.add(thisContinualValues);
                                    }
                                } else {// 变量离散
                                    if (!discreteVariables.contains(nuclear.variable)) {// 变量未存储
                                        discreteVariables.add(nuclear.variable);
                                        ArrayList<String> thisDiscreteRanges = new ArrayList<String>();
                                        String[] thisRanges = input_T.element("range").getText().replace(" ", "")
                                                .split(",");
                                        for (String thisRange : thisRanges) {
                                            if (thisRange.contains("="))
                                                thisDiscreteRanges
                                                        .add(thisRange.substring(0, thisRange.indexOf("=")));
                                            else
                                                thisDiscreteRanges.add(thisRange);
                                        }
                                        discreteRanges.add(thisDiscreteRanges);
                                    }
                                }
                            }
                        }
                        while (tableIterator_T.hasNext()) {// 判断是否为中间、输出变量
                            Element table_T = (Element) tableIterator_T.next();
                            String name = table_T.attributeValue("name");
                            if (nuclear.variable.equals(name)) {// 变量名是中间、输出变量
                                if (table_T.element("range").getText().contains("\\.\\.")) {// 变量连续
                                    if (continualVariables.contains(nuclear.variable)) {// 变量已存储
                                        if (!continualValues.get(continualVariables.indexOf(nuclear.variable))
                                                .contains(nuclear.value)) {// 值未存储
                                            ArrayList<String> thisContinualValues = continualValues
                                                    .get(continualVariables.indexOf(nuclear.variable));
                                            thisContinualValues.add(nuclear.value);
                                            continualValues.set(continualVariables.indexOf(nuclear.variable),
                                                    thisContinualValues);
                                        }
                                    } else {// 变量未存储
                                        continualVariables.add(nuclear.variable);
                                        ArrayList<String> thisContinualValues = new ArrayList<String>();
                                        thisContinualValues.add(nuclear.value);
                                        continualValues.add(thisContinualValues);
                                    }
                                } else {// 变量离散
                                    if (!discreteVariables.contains(nuclear.variable)) {// 变量未存储
                                        discreteVariables.add(nuclear.variable);
                                        ArrayList<String> thisDiscreteRanges = new ArrayList<String>();
                                        String[] thisRanges = table_T.element("range").getText().replace(" ", "")
                                                .split(",");
                                        for (String thisRange : thisRanges) {
                                            if (thisRange.contains("="))
                                                thisDiscreteRanges
                                                        .add(thisRange.substring(0, thisRange.indexOf("=")));
                                            else
                                                thisDiscreteRanges.add(thisRange);
                                        }
                                        discreteRanges.add(thisDiscreteRanges);
                                    }
                                }
                            }
                        }
                        while (stateMachineIterator_T.hasNext()) {// 判断是否为模式集
                            Element stateMachine_T = (Element) stateMachineIterator_T.next();
                            String name = stateMachine_T.attributeValue("name");
                            if (nuclear.variable.equals(name)) {
                                if (!discreteVariables.contains(nuclear.variable)) {// 变量未存储
                                    discreteVariables.add(nuclear.variable);
                                    ArrayList<String> thisDiscreteRanges = new ArrayList<String>();
                                    Element stateListNode = stateMachine_T.element("stateList");
                                    Iterator statesIterator = stateListNode.elementIterator();
                                    while (statesIterator.hasNext()) {
                                        Element state = (Element) statesIterator.next();
                                        thisDiscreteRanges.add(state.attributeValue("name") + "");
                                    }
                                    discreteRanges.add(thisDiscreteRanges);
                                }
                            }
                        }
                    }
                    andTree.add(nuclear);
                }
                orTree.add(andTree);
            }
            nuclearTreeForEachRow.add(orTree);
        }
        sortContinualValues(continualValues);
        int variableNumber = continualVariables.size() + discreteVariables.size();
        variableRanges = new int[variableNumber];
        for (int i = 0; i < variableNumber; i++) {
            if (i < continualVariables.size()) {
                variableRanges[i] = 2 * continualValues.get(i).size() + 1;
            } else {
                variableRanges[i] = discreteRanges.get(i - continualVariables.size()).size();
            }
        }
        coder = new Coder(variableNumber, variableRanges);
        for (long l = 0; l < coder.codeLimit; l++) {
            if (!coder.decode(l).containsZero())
                outputForEachState.add(new HashSet<Integer>());
            else
                outputForEachState.add(null);
        }
    }



    public void buildStates() {
        for (ArrayList<ArrayList<NuclearCondition>> orTree : nuclearTreeForEachRow) {// 遍历每行的析取范式树
            if (orTree.get(0).get(0).isTrue) {// 如果第一个合取式的第一个原子条件为true，则整个条件就是true
                for (Set<Integer> outputForThisState : outputForEachState) {
                    outputForThisState.add(
                            outputRanges.indexOf(behaviorForEachRow.get(nuclearTreeForEachRow.indexOf(orTree))));
                }
                continue;
            } else if (orTree.get(0).get(0).isFalse) {// 如果第一个合取式的第一个原子条件为false，则整个条件就是false
                continue;
            }
            ArrayList<State> stateCollection = new ArrayList<State>();// 每行的析取范式树新建一个状态集合
            for (ArrayList<NuclearCondition> andTree : orTree) {// 遍历析取范式树的每个合取式
                ArrayList<State> thisAndTreeStates = new ArrayList<State>();// 对于每个合取式，先新建一个临时列表存储要加入该行条件的状态集合的状态
                ArrayList<Integer>[] valuesForEachVariable = new ArrayList[continualVariables.size()
                        + discreteVariables.size()];
                boolean[] isSetForEachVariable = new boolean[continualVariables.size() + discreteVariables.size()];

                buildAndTreeStates(andTree,valuesForEachVariable,isSetForEachVariable); //解析and树

                thisAndTreeStates.add(new State(continualVariables.size() + discreteVariables.size()));
                for (int i = 0; i < continualVariables.size() + discreteVariables.size(); i++) {
                    if (isSetForEachVariable[i]) {
                        int previousCount = thisAndTreeStates.size();
                        for (int j = 0; j < previousCount; j++) {
                            State thisState = thisAndTreeStates.get(j);
                            boolean isFirstSet = false;
                            for (Integer value : valuesForEachVariable[i]) {
                                if (!isFirstSet) {
                                    thisState.state[i] = value.intValue();
                                    isFirstSet = true;
                                    thisAndTreeStates.set(j, thisState);
                                } else {
                                    State newState = new State(thisState);
                                    newState.state[i] = value.intValue();
                                    thisAndTreeStates.add(newState);
                                }
                            }
                        }
                    }
                }
                for (State thisState : thisAndTreeStates) {
                    if (!stateCollection.contains(thisState))
                        stateCollection.add(thisState);
                }
            }
            for (State thisState : stateCollection) {
                for (long l = 0; l < coder.codeLimit; l++) {
                    State s = coder.decode(l);
                    if (!s.containsZero() && s.almostEquals(thisState))
                        outputForEachState.get((int) l).add(outputRanges
                                .indexOf(behaviorForEachRow.get(nuclearTreeForEachRow.indexOf(orTree))));
                }
            }
        }
    }

    public void buildAndTreeStates(ArrayList<NuclearCondition> andTree, ArrayList<Integer>[] valuesForEachVariable, boolean[] isSetForEachVariable){
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
                for (Integer value : thisVariableValues) {

                }

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

