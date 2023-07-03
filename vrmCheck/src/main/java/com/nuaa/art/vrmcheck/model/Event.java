package com.nuaa.art.vrmcheck.model;

import com.nuaa.art.vrm.model.model.VRMOfXML;
import com.nuaa.art.vrmcheck.common.utils.VariableUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 事件
 *
 * @author konsin
 * @date 2023/06/21
 */
public class Event {
    public VRMOfXML vrmModel;
    public ArrayList<ArrayList<ArrayList<ArrayList<NuclearCondition>>[]>> nuclearTreeForEachRow;// 每个事件的每个AND事件的两个条件的原子命题树
    public ArrayList<String> continualVariables;// 条件中的数值型变量
    public ArrayList<String> discreteVariables;// 条件中的枚举型变量
    public ArrayList<ContinualRange> continualRanges;
    public ArrayList<ArrayList<String[]>> eventOperatorsForEachRow;
    public ArrayList<ArrayList<String>> continualValues;// 每个数值型变量在各条件中比较的值
    public ArrayList<ArrayList<String>> discreteRanges;// 每个枚举型变量的值域
    public ArrayList<String> behaviorForEachRow;// 每行的赋值
    public ArrayList<ArrayList<ArrayList<Long>[]>> timeScenarios;// 每行事件的每个AND事件的两个条件对应的场景集合
    public ArrayList<ArrayList<Long>[]> scenarios;// 每行事件对应两个前后场景集合
    public int[] variableRanges;
    public Coder coder;// 编码器

    // 构造方法，基于事件列表和各事件的输出值作为参数，用于事件分析，此处调用了条件转换器的构造方法1，复用那部分代码生成条件的原子命题树
    public Event(VRMOfXML vrmModel, ArrayList<String> eventsForEachRow, ArrayList<String> assignmentForEachRow) {
        this.vrmModel = vrmModel;
        behaviorForEachRow = new ArrayList<String>(assignmentForEachRow);
        nuclearTreeForEachRow = new ArrayList<ArrayList<ArrayList<ArrayList<NuclearCondition>>[]>>();
        continualVariables = new ArrayList<String>();
        discreteVariables = new ArrayList<String>();
        continualValues = new ArrayList<ArrayList<String>>();
        discreteRanges = new ArrayList<ArrayList<String>>();
        scenarios = new ArrayList<ArrayList<Long>[]>();
        continualRanges = new ArrayList<ContinualRange>();
        eventOperatorsForEachRow = new ArrayList<ArrayList<String[]>>();
        timeScenarios = new ArrayList<ArrayList<ArrayList<Long>[]>>();

        for (String event : eventsForEachRow) {
            String[] andEvents = event.replace(" ", "").split("\\}\\{");// 分离AND事件

            andEvents[0] = andEvents[0].substring(1);
            andEvents[andEvents.length - 1] = andEvents[andEvents.length - 1].substring(0,
                    andEvents[andEvents.length - 1].length() - 1);
            ArrayList<String> andEventForEachRow = new ArrayList<String>();// 该行各个AND事件
            for (int i = 0; i < andEvents.length; i++) {// 存储AND事件
                if ((!andEvents[i].equals("never")) && (!andEvents[i].equals("NEVER"))
                        && (!andEvents[i].contains("when")) && (!andEvents[i].contains("WHEN"))
                        && (!andEvents[i].contains("while")) && (!andEvents[i].contains("WHILE"))
                        && (!andEvents[i].contains("where")) && (!andEvents[i].contains("WHERE"))) {
                    if (andEvents[i].charAt(andEvents[i].length() - 1) == '_')
                        andEvents[i] = andEvents[i].substring(0, andEvents[i].length() - 1);
                    andEvents[i] = andEvents[i] + "when(true)";// 处理无监视事件，添加when(true)
                }
                andEventForEachRow.add(andEvents[i]);
            }
            if ((!andEventForEachRow.contains("never")) && (!andEventForEachRow.contains("NEVER"))) {// 仅存储非never行的事件信息
                ArrayList<ArrayList<ArrayList<NuclearCondition>>[]> andEventsForThisRow = new ArrayList<ArrayList<ArrayList<NuclearCondition>>[]>();
                ArrayList<String[]> andEtagsForEachRow = new ArrayList<String[]>();// 该行各个AND事件的事件类型
                Iterator<String> andEventForEachRowIterator = andEventForEachRow.iterator();
                while (andEventForEachRowIterator.hasNext()) {
                    ArrayList<ArrayList<NuclearCondition>>[] twoNuclearTreesForThisAndEvnet = new ArrayList[2];
                    String andEvent = andEventForEachRowIterator.next();// 其中一个AND事件
                    String etag = "";// 此AND事件标识
                    String eguard = "";// 此AND事件监视
                    if (andEvent.contains("when") || andEvent.contains("WHEN"))
                        eguard = "when";
                    else if (andEvent.contains("where") || andEvent.contains("WHERE"))
                        eguard = "where";
                    else
                        eguard = "while";// 解析事件监视
                    if (andEvent.contains("@T"))
                        etag = "@T";
                    else if (andEvent.contains("@F"))
                        etag = "@F";
                    else
                        etag = "@C";// 解析事件标识
                    String[] etags = { etag, eguard };// 此AND事件的事件类型
                    String[] twoConditions;
                    if (andEvent.contains(eguard))
                        twoConditions = andEvent.split(eguard);
                    else
                        twoConditions = andEvent.split(eguard.toUpperCase());// 使用监视分离两个条件
                    if (twoConditions[0].length() == 4)
                        twoConditions[0] = "";
                    else
                        twoConditions[0] = twoConditions[0].substring(3, twoConditions[0].length() - 1)
                                .replace("(","").replace(")","");// 剥离条件1的事件标识和括号
                    if (twoConditions[1].length() == 2)
                        twoConditions[1] = "";
                    else
                        twoConditions[1] = twoConditions[1].replace("(","").replace(")","");// 剥离条件2的括号
                    andEtagsForEachRow.add(etags);// 将此AND事件的事件类型存入上层集合
                    SubConditionOfEvent cp = new SubConditionOfEvent(vrmModel, twoConditions);  // 来获取事件子条件中的关键量和值域
                    for (int i = 0; i < 2; i++) {
                        twoNuclearTreesForThisAndEvnet[i] = cp.nuclearTreeForEachRow.get(i);
                    }
                    andEventsForThisRow.add(twoNuclearTreesForThisAndEvnet);
                    for (int i = 0; i < cp.continualVariables.size(); i++) {
                        if (!this.continualVariables.contains(cp.continualVariables.get(i))) {
                            continualVariables.add(cp.continualVariables.get(i));
                            continualValues.add(cp.continualValues.get(i));
                            continualRanges.add(cp.continualRanges.get(i));
                        } else {
                            int variableIndex = continualVariables.indexOf(cp.continualVariables.get(i));
                            ArrayList<String> thisContinualValues = cp.continualValues.get(i);
                            ArrayList<String> continualValuesOfVariable = continualValues.get(variableIndex);
                            for (String continualValue : thisContinualValues) {
                                if (!continualValuesOfVariable.contains(continualValue))
                                    continualValuesOfVariable.add(continualValue);
                            }
                            continualValues.set(variableIndex, continualValuesOfVariable);
                        }
                    }
                    for (int i = 0; i < cp.discreteVariables.size(); i++) {
                        if (!this.discreteVariables.contains(cp.discreteVariables.get(i))) {
                            discreteVariables.add(cp.discreteVariables.get(i));
                            discreteRanges.add(cp.discreteRanges.get(i));
                        } else {
                            int variableIndex = discreteVariables.indexOf(cp.discreteVariables.get(i));
                            ArrayList<String> thisDiscreteRanges = cp.discreteRanges.get(i);
                            ArrayList<String> discreteValuesOfVariable = discreteRanges.get(variableIndex);
                            for (String discreteRange : thisDiscreteRanges) {
                                if (!discreteValuesOfVariable.contains(discreteRange))
                                    discreteValuesOfVariable.add(discreteRange);
                            }
                            discreteRanges.set(variableIndex, discreteValuesOfVariable);
                        }
                    }
                }
                nuclearTreeForEachRow.add(andEventsForThisRow);
                eventOperatorsForEachRow.add(andEtagsForEachRow);
            }
        }
        VariableUtils.sortContinualValues(continualValues);
        handleAtCEvent();
        int variableNumber = continualVariables.size() + discreteVariables.size();
        // 生成全体关键变量的离散化值域
        variableRanges = VariableUtils.rangeDiscretization(continualVariables.size(), discreteVariables.size(), continualValues, discreteRanges);
        //生成场景编码器
        coder = new Coder(variableNumber, variableRanges);
    }

    // 将事件内的每个@C类AND分解为各一个@T类和@F类，并复制一次该事件
    public void handleAtCEvent() {
        for (int i = 0; i < eventOperatorsForEachRow.size(); i++) {// 遍历各行事件类型，对@C事件，将其信息拷贝为两份，分别设为@T和@F，进行保存
            ArrayList<String[]> etagsList = eventOperatorsForEachRow.get(i);// 本行各AND事件的事件类型
            ArrayList<Integer> indexOfAndAtCEvent = new ArrayList<Integer>();// 本行AND事件中所有@C事件索引值
            for (int j = 0; j < etagsList.size(); j++) {// 遍历各AND事件的事件类型
                String[] etags = etagsList.get(j);// 此AND事件的事件类型
                if (etags[0].equals("@C")) {
                    indexOfAndAtCEvent.add(j);// 若为@C，将此索引值加入集合
                }
            }
            for (int j = 0; j < Math.pow(2, indexOfAndAtCEvent.size()); j++) {// 对于每个@C的AND事件，保持其他不变拷贝为两份，将自身改为@F和@T，一共拷贝为2的x次方份，x为@C的AND事件数量
                if (j == 0) {// 对于拷贝源，保持其在集合中，并将其所有@C改为@T
                    for (int index : indexOfAndAtCEvent) {
                        ArrayList<String[]> thisEtagsList = eventOperatorsForEachRow.get(i);// 该行各AND事件的事件类型
                        String[] thisEtags = thisEtagsList.get(index);// 索引处AND事件的事件类型
                        thisEtags[0] = "@T";// 改为@T
                        thisEtagsList.set(index, thisEtags);// 存回
                        eventOperatorsForEachRow.set(i, thisEtagsList);// 存回
                    }
                } else {
                    ArrayList<String[]> newEtagsList = new ArrayList<String[]>();// 新添行各AND事件的事件类型
                    ArrayList<ArrayList<ArrayList<NuclearCondition>>[]> newNuclearTree = new ArrayList<ArrayList<ArrayList<NuclearCondition>>[]>();// 新添行各AND事件的两个条件
                    String binary = Integer.toBinaryString(j);// 将j存储为二进制字符串，通过每一位的编码判断各@C的AND事件应拷贝为@T或@F
                    if (binary.length() < indexOfAndAtCEvent.size()) {// 令binary位数与@C的AND事件个数相同，使其一一对应
                        int subLength = indexOfAndAtCEvent.size() - binary.length();
                        for (int k = 0; k < subLength; k++) {
                            binary = "0" + binary;
                        }
                    }
                    for (int k = 0; k < etagsList.size(); k++) {
                        if (!indexOfAndAtCEvent.contains(Integer.valueOf(k))) {// 对于不为@C的AND事件，直接复制
                            String[] etags = new String[2];// 新添行中第k个AND事件的事件类型
                            System.arraycopy(etagsList.get(k), 0, etags, 0, 2);
                            newEtagsList.add(etags);// 存入新添行集合
                        } else {// 对于为@C的AND事件，判断此时j的二进制编码，对应索引值为0的AND事件复制为@T，否则为@F
                            String[] etags = new String[2];// 新添行中第k个AND事件的事件类型
                            System.arraycopy(etagsList.get(k), 0, etags, 0, 2);
                            if (binary.charAt(indexOfAndAtCEvent.indexOf(Integer.valueOf(k))) == '0')
                                etags[0] = "@T";
                            else
                                etags[0] = "@F";
                            newEtagsList.add(etags);// 存入新添行集合
                        }
                        ArrayList<ArrayList<NuclearCondition>>[] nuclearTrees = new ArrayList[2];
                        nuclearTrees[0] = nuclearTreeForEachRow.get(i).get(k)[0];
                        nuclearTrees[1] = nuclearTreeForEachRow.get(i).get(k)[1];// 新添行中第k个AND事件的两个条件
                        newNuclearTree.add(nuclearTrees);// 存入新添行集合
                    }
                    String newBehavior = behaviorForEachRow.get(i);// 新添行的目标模式
                    eventOperatorsForEachRow.add(newEtagsList);
                    nuclearTreeForEachRow.add(newNuclearTree);
                    behaviorForEachRow.add(newBehavior);
                }
            }
        }
    }

    // 将事件转换为时序场景集对（1.将每个AND事件的两个条件转换为场景集对 2.将场景集对按照事件的逻辑语义进行集合运算，转换为时序场景集对）
    public void parseEventIntoStates() {
        if (continualVariables.size() + discreteVariables.size() != 0) {
            buildTimeScenarios();
            buildScenarios();
            //outputStates();
        }
    }

    // 将每个AND事件的两个条件转换为场景集对，此处调用了条件转化器的构造方法2和将条件转换为场景集的方法，复用那部分代码直接获取条件的场景集
    public void buildTimeScenarios() {
        for (ArrayList<ArrayList<ArrayList<NuclearCondition>>[]> nuclearTreeForThisRow : nuclearTreeForEachRow) {
            ArrayList<ArrayList<Long>[]> timeStatesForThisRow = new ArrayList<ArrayList<Long>[]>();
            for (ArrayList<ArrayList<NuclearCondition>>[] nuclearTreeForThisAnd : nuclearTreeForThisRow) {
                SubConditionOfEvent cp = new SubConditionOfEvent(vrmModel, nuclearTreeForThisAnd, continualVariables,
                        discreteVariables, continualRanges, continualValues, discreteRanges, coder);
                cp.parseConditionIntoScenarios();
                ArrayList<Long>[] timeStatesForThisAnd = new ArrayList[2];
                timeStatesForThisAnd[0] = cp.scenariosCode.get(0);
                timeStatesForThisAnd[1] = cp.scenariosCode.get(1);
                timeStatesForThisRow.add(timeStatesForThisAnd);
            }
            timeScenarios.add(timeStatesForThisRow);
        }
    }

    // 将每个合取式转换为一个未确定所有取值的场景
    public void buildScenarios() {
        for (int i = 0; i < timeScenarios.size(); i++) {
            ArrayList<ArrayList<Long>[]> timeStatesForThisRow = timeScenarios.get(i);
            ArrayList<ArrayList<Long>[]> statesForThisRow = new ArrayList<ArrayList<Long>[]>();
            for (int j = 0; j < timeStatesForThisRow.size(); j++) {
                ArrayList<Long>[] timeStatesForThisAnd = timeStatesForThisRow.get(j);
                ArrayList<Long>[] statesForThisAnd = new ArrayList[2];
                statesForThisAnd[0] = new ArrayList<Long>();
                statesForThisAnd[1] = new ArrayList<Long>();
                // ArrayList<State> wholeCollection = new ArrayList<State>();
                // State wholeState = new State(continualVariables.size() +
                // discreteVariables.size());
                // wholeCollection.add(wholeState);
                // wholeCollection = appendInconsidered(wholeCollection);
                ArrayList<Long> complementaryOfCollectionOne = new ArrayList<Long>();
                for (long l = 0; l < coder.codeLimit; l++) {
                    boolean isZeroIn = false;
                    Scenario thisScenario = coder.decode(l);
                    for (int k = 0; k < thisScenario.variableNumber; k++) {
                        if (thisScenario.scenario[k] == 0)
                            isZeroIn = true;
                    }
                    if (isZeroIn) {
                        continue;
                    }
                    if (!timeStatesForThisAnd[0].contains(l))
                        complementaryOfCollectionOne.add(l);
                }
                String eventOperator = eventOperatorsForEachRow.get(i).get(j)[0];
                String guardOperator = eventOperatorsForEachRow.get(i).get(j)[1];
                if (eventOperator.equals("@T")) {
                    if (guardOperator.equals("when")) {
                        for (Long state : complementaryOfCollectionOne) {
                            if (timeStatesForThisAnd[1].contains(state))
                                statesForThisAnd[0].add(state);
                        }
                        for (Long state : timeStatesForThisAnd[0]) {
                            statesForThisAnd[1].add(state);
                        }
                    } else if (guardOperator.equals("where")) {
                        for (Long state : complementaryOfCollectionOne) {
                            statesForThisAnd[0].add(state);
                        }
                        for (Long state : timeStatesForThisAnd[0]) {
                            if (timeStatesForThisAnd[1].contains(state))
                                statesForThisAnd[1].add(state);
                        }
                    } else {
                        for (Long state : complementaryOfCollectionOne) {
                            if (timeStatesForThisAnd[1].contains(state))
                                statesForThisAnd[0].add(state);
                        }
                        for (Long state : timeStatesForThisAnd[0]) {
                            if (timeStatesForThisAnd[1].contains(state))
                                statesForThisAnd[1].add(state);
                        }
                    }
                } else {
                    if (guardOperator.equals("when")) {
                        for (Long state : timeStatesForThisAnd[0]) {
                            if (timeStatesForThisAnd[1].contains(state))
                                statesForThisAnd[0].add(state);
                        }
                        for (Long state : complementaryOfCollectionOne) {
                            statesForThisAnd[1].add(state);
                        }
                    } else if (guardOperator.equals("where")) {
                        for (Long state : timeStatesForThisAnd[0]) {
                            statesForThisAnd[0].add(state);
                        }
                        for (Long state : complementaryOfCollectionOne) {
                            if (timeStatesForThisAnd[1].contains(state))
                                statesForThisAnd[1].add(state);
                        }
                    } else {
                        for (Long state : timeStatesForThisAnd[0]) {
                            if (timeStatesForThisAnd[1].contains(state))
                                statesForThisAnd[0].add(state);
                        }
                        for (Long state : complementaryOfCollectionOne) {
                            if (timeStatesForThisAnd[1].contains(state))
                                statesForThisAnd[1].add(state);
                        }
                    }
                }
                statesForThisRow.add(statesForThisAnd);
            }
            ArrayList<Long>[] stateCollectionForThisRow = new ArrayList[2];
            stateCollectionForThisRow[0] = new ArrayList<Long>(statesForThisRow.get(0)[0]);
            stateCollectionForThisRow[1] = new ArrayList<Long>(statesForThisRow.get(0)[1]);
            for (int k = 1; k < statesForThisRow.size(); k++) {
                stateCollectionForThisRow[0].retainAll(statesForThisRow.get(k)[0]);
                stateCollectionForThisRow[1].retainAll(statesForThisRow.get(k)[1]);
            }
            scenarios.add(stateCollectionForThisRow);
        }
    }

     //输出场景集到Eclipse的Console，调试用
    public void outputStates() {
        for (int i = 0; i < nuclearTreeForEachRow.size(); i++) {
            ArrayList<ArrayList<ArrayList<NuclearCondition>>[]> thisRow = nuclearTreeForEachRow.get(i);
            String event = "";
            for (ArrayList<ArrayList<NuclearCondition>>[] twoNuclearTrees : thisRow) {
                event += eventOperatorsForEachRow.get(i).get(thisRow.indexOf(twoNuclearTrees))[0];
                event += "(";
                for (ArrayList<NuclearCondition> andTree : twoNuclearTrees[0]) {
                    event += "(";
                    for (NuclearCondition nuclear : andTree) {
                        event += nuclear.variable + nuclear.operator + nuclear.value + "&&";
                    }
                    event = event.substring(0, event.length() - 2);
                    event += ")||";
                }
                event = event.substring(0, event.length() - 2);
                event += ")";
                event += eventOperatorsForEachRow.get(i).get(thisRow.indexOf(twoNuclearTrees))[1];
                event += "(";
                for (ArrayList<NuclearCondition> andTree : twoNuclearTrees[1]) {
                    event += "(";
                    for (NuclearCondition nuclear : andTree) {
                        event += nuclear.variable + nuclear.operator + nuclear.value + "&&";
                    }
                    event = event.substring(0, event.length() - 2);
                    event += ")||";
                }
                event = event.substring(0, event.length() - 2);
                event += ")";
                event += " AND ";
            }
            event = event.substring(0, event.length() - 5);
            System.out.println("Event:" + event);
            ArrayList<Long>[] stateCollection = scenarios.get(i);
            String variables = "variables:";
            for (String variableName : continualVariables) {
                variables += variableName + ",";
            }
            for (String variableName : discreteVariables) {
                variables += variableName + ",";
            }
            variables = variables.substring(0, variables.length() - 1);
            System.out.println(variables);
            for (Long thisState : stateCollection[0]) {
                System.out.print(thisState + "\t");

            }
            System.out.println();
            System.out.println(variables);
            for (Long thisState : stateCollection[1]) {
                System.out.print(thisState + "\t");

            }
            System.out.println();
        }

    }

}

