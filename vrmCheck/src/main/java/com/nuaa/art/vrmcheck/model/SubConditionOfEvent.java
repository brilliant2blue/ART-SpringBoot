package com.nuaa.art.vrmcheck.model;

import com.nuaa.art.vrm.model.model.VRMOfXML;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;

import static com.nuaa.art.vrmcheck.common.utils.SortContinualValues.sortContinualValues;

public class SubConditionOfEvent extends Condition{
    public ArrayList<ArrayList<Long>> statesCode;



    // 构造方法1，基于条件表每行的条件语句构成的String[]作为参数，被事件转换器调用，替事件分析构造各条件的原子命题树
    public SubConditionOfEvent(VRMOfXML vrmModel, String[] conditionsForEachRowArray) {
        super();
        this.vrmModel = vrmModel;
        ArrayList<String> assignmentForEachRow = new ArrayList<String>();
        ArrayList<String> conditionsForEachRow = new ArrayList<String>();
        for (String condition : conditionsForEachRowArray) {
            assignmentForEachRow.add("");
            conditionsForEachRow.add(condition);
        }
        getConditionTableInformation(conditionsForEachRow, assignmentForEachRow);
    }

    // 构造方法2，基于事件转换器提供的信息作为参数，被事件转换器调用
    public SubConditionOfEvent(VRMOfXML vrmModel, ArrayList<ArrayList<NuclearCondition>>[] twoNuclearTrees,
                               ArrayList<String> continualVariables, ArrayList<String> discreteVariables,
                               ArrayList<ContinualRange> continualRanges, ArrayList<ArrayList<String>> continualValues,
                               ArrayList<ArrayList<String>> discreteRanges, Coder coder) {
        super();
        this.vrmModel = vrmModel;
        this.behaviorForEachRow = new ArrayList<String>();
        this.nuclearTreeForEachRow = new ArrayList<ArrayList<ArrayList<NuclearCondition>>>();
        this.continualVariables = continualVariables;
        this.discreteVariables = discreteVariables;
        this.continualValues = continualValues;
        this.discreteRanges = discreteRanges;
        this.continualRanges = continualRanges;
        this.nuclearTreeForEachRow.add(twoNuclearTrees[0]);
        this.nuclearTreeForEachRow.add(twoNuclearTrees[1]);
        this.coder = coder;
        statesCode = new ArrayList<ArrayList<Long>>();
    }

    // 将条件转换为场景集（1.将每个合取式转换为一个未确定所有取值的场景 2.确定场景中的未确定取值，此操作会增加场景的数量）
    public void parseConditionIntoStates() {
        if (continualVariables.size() + discreteVariables.size() != 0) {
            buildStates();
            appendInconsidered();
            //outputStates();
        }
    }

    // 根据条件列表和各条件的输出值，获取条件转换器的各种属性
    public void getConditionTableInformation(ArrayList<String> conditionsForEachRow,
                                             ArrayList<String> assignmentForEachRow) {
        behaviorForEachRow = new ArrayList<String>(assignmentForEachRow);
        nuclearTreeForEachRow = new ArrayList<ArrayList<ArrayList<NuclearCondition>>>();
        continualVariables = new ArrayList<String>();
        discreteVariables = new ArrayList<String>();
        continualValues = new ArrayList<ArrayList<String>>();
        discreteRanges = new ArrayList<ArrayList<String>>();
        continualRanges = new ArrayList<ContinualRange>();
        statesCode = new ArrayList<ArrayList<Long>>();
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
    }

    // 查找条件完整性错误（一个）
    public ConditionIntegrityError fineIntegrityError() {
        ConditionIntegrityError cie = new ConditionIntegrityError();
        if (continualVariables.size() + discreteVariables.size() != 0) {
            ArrayList<Long> disjunctedCollection = new ArrayList<Long>();
            for (int i = 0; i < statesCode.size(); i++) {
                for (Long thisState : statesCode.get(i)) {
                    if (!disjunctedCollection.contains(thisState))
                        disjunctedCollection.add(thisState);
                }
            }
            for (long l = 0; l < coder.codeLimit; l++) {
                if (!disjunctedCollection.contains(l)) {
                    ConcreteState thisConcreteState = new ConcreteState(
                            continualVariables.size() + discreteVariables.size());
                    State thisState = coder.decode(l);
                    boolean isZeroIn = false;
                    for (int i = 0; i < thisState.variableNumber; i++) {
                        if (thisState.state[i] == 0)
                            isZeroIn = true;
                    }
                    if (isZeroIn) {
                        continue;
                    }
                    for (int k = 0; k < thisState.variableNumber; k++) {
                        if (k < continualVariables.size()) {
                            int value = thisState.state[k];
                            if (value == 1) {
                                thisConcreteState.concreteState[k] = "(" + continualRanges.get(k).lowLimit + ","
                                        + continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                            } else if (value == continualValues.get(k).size() * 2 + 1) {
                                thisConcreteState.concreteState[k] = "("
                                        + continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                        + continualRanges.get(k).highLimit + ")";
                            } else if (value % 2 == 0) {
                                thisConcreteState.concreteState[k] = continualValues.get(k).get(value / 2 - 1);
                            } else {
                                thisConcreteState.concreteState[k] = "("
                                        + continualValues.get(k).get((value - 1) / 2 - 1) + ","
                                        + continualValues.get(k).get((value + 1) / 2 - 1) + ")";
                            }
                        } else {
                            int value = thisState.state[k];
                            thisConcreteState.concreteState[k] = discreteRanges.get(k - continualVariables.size())
                                    .get(value - 1);
                        }
                    }
                    cie.lostStates.add(thisConcreteState);
                }
            }
        } else {
            int trueCount = 0;
            for (int i = 0; i < nuclearTreeForEachRow.size(); i++) {
                if (nuclearTreeForEachRow.get(i).get(0).get(0).isTrue)
                    trueCount++;
            }
            if (trueCount == 0) {
                cie.isTrueNotExist = true;
            }
        }
        return cie;
    }

    // 确定场景中的未确定取值，此操作会增加场景的数量
    public void appendInconsidered() {
        for (int i = 0; i < statesCode.size(); i++) {
            ArrayList<Long> stateCollection = statesCode.get(i);
            ArrayList<Long> stateCodeCollection;

            String condition = "";
            ArrayList<ArrayList<NuclearCondition>> a = nuclearTreeForEachRow.get(i);
            for (ArrayList<NuclearCondition> b : a) {
                condition += "(";
                for (NuclearCondition c : b) {
                    condition += c.variable + c.operator + c.value;
                    condition += "&&";
                }
                condition = condition.substring(0, condition.length() - 2);
                condition += ")||";
            }
            condition = condition.substring(0, condition.length() - 2);
            System.out.println("Condition:" + condition);
            System.out.println("Size:" + stateCollection.size());
            for (Long shit : stateCollection) {
                System.out.println("State:" + shit);
            }
            stateCodeCollection = appendInconsideredToCode(stateCollection);
            statesCode.set(i, stateCodeCollection);

        }
    }

    // 确定单条条件场景中的未确定取值，也可用来构建场景全集
    public ArrayList<Long> appendInconsideredToCode(ArrayList<Long> stateCollection) {
        ArrayList<Long> stateCollectionSingle = new ArrayList<Long>();
        for (int j = 0; j < stateCollection.size(); j++) {
            long code = stateCollection.get(j);
            State thisState = coder.decode(code);
            System.out.println("oldState:" + code);
            ArrayList<Integer> inconsidered = new ArrayList<Integer>();
            for (int i = 0; i < thisState.state.length; i++) {
                if (thisState.state[i] == 0) {
                    inconsidered.add(i);
                }
            }
            stateCollectionSingle = coder.appendInconsidered(stateCollectionSingle, code, inconsidered);
        }
        return stateCollectionSingle;

    }

    // 输出场景集到Eclipse的Console，调试用
//    public void outputStates() {
//        for (int i = 0; i < statesCode.size(); i++) {
//            String condition = "";
//            ArrayList<ArrayList<NuclearCondition>> a = nuclearTreeForEachRow.get(i);
//            for (ArrayList<NuclearCondition> b : a) {
//                condition += "(";
//                for (NuclearCondition c : b) {
//                    condition += c.variable + c.operator + c.value;
//                    condition += "&&";
//                }
//                condition = condition.substring(0, condition.length() - 2);
//                condition += ")||";
//            }
//            condition = condition.substring(0, condition.length() - 2);
//            System.out.println("Condition:" + condition);
//            ArrayList<Long> stateCollection = statesCode.get(i);
//            String variables = "variables:";
//            for (String variableName : continualVariables) {
//                variables += variableName + ",";
//            }
//            for (String variableName : discreteVariables) {
//                variables += variableName + ",";
//            }
//            variables = variables.substring(0, variables.length() - 1);
//            System.out.println(variables);
//            for (Long thisState : stateCollection) {
//                System.out.print(thisState + "\t");
//            }
//            System.out.println();
//        }
//    }

    @Override
    // 将每个合取式转换为一个未确定所有取值的场景
    public void buildStates() {
        for (ArrayList<ArrayList<NuclearCondition>> orTree : nuclearTreeForEachRow) {// 遍历每行的析取范式树
            ArrayList<Long> stateCollection = new ArrayList<Long>();// 每行的析取范式树新建一个状态集合
            for (ArrayList<NuclearCondition> andTree : orTree) {// 遍历析取范式树的每个合取式
                ArrayList<Long> thisAndTreeStates = new ArrayList<Long>();// 对于每个合取式，先新建一个临时列表存储要加入该行条件的状态集合的状态
                ArrayList<Integer>[] valuesForEachVariable = new ArrayList[continualVariables.size()
                        + discreteVariables.size()];
                boolean[] isSetForEachVariable = new boolean[continualVariables.size() + discreteVariables.size()];
                if (andTree.get(0).isTrue) {
                    thisAndTreeStates.add(0l);
                    thisAndTreeStates = appendInconsideredToCode(thisAndTreeStates);
                } else if (andTree.get(0).isFalse) {

                } else {
                    buildAndTreeStates(andTree,valuesForEachVariable,isSetForEachVariable);
                    thisAndTreeStates.add(0l);
                    for (int i = 0; i < continualVariables.size() + discreteVariables.size(); i++) {
                        if (isSetForEachVariable[i]) {
                            int previousCount = thisAndTreeStates.size();
                            for (int j = 0; j < previousCount; j++) {
                                State thisState = coder.decode(thisAndTreeStates.get(j));
                                boolean isFirstSet = false;
                                for (Integer value : valuesForEachVariable[i]) {
                                    if (!isFirstSet) {
                                        thisState.state[i] = value.intValue();
                                        isFirstSet = true;
                                        thisAndTreeStates.set(j, coder.encode(thisState));
                                    } else {
                                        State newState = new State(thisState);
                                        newState.state[i] = value.intValue();
                                        thisAndTreeStates.add(coder.encode(newState));
                                    }
                                }
                            }
                        }
                    }
                }
                for (Long thisState : thisAndTreeStates) {
                    if (!stateCollection.contains(thisState))
                        stateCollection.add(thisState);
                }
            }
            statesCode.add(stateCollection);
        }
    }
}

