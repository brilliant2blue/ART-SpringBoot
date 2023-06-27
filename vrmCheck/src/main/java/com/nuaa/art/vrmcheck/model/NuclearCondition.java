package com.nuaa.art.vrmcheck.model;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 表示原子命题的类
 *
 * @author konsin
 * @date 2023/06/12
 */
public class NuclearCondition {
    public String variable = "";
    public String operator = "";
    public String value = "";
    public boolean isTrue = false;
    public boolean isFalse = false;

    /**
     * 原子条件语句解析为原子条件
     *
     * @param content 条件内容
     * @return {@link Boolean} 如果是永真或者永假则返回true
     */
    public Boolean syntacticParser(String content){
        if (content.equals("()") || content.equals(""))
            content = "";
        else {
            if (content.indexOf('(') == 0)
                content = content.substring(1);
            if (content.lastIndexOf(')') == content.length() - 1)
                content = content.substring(0, content.length() - 1);
        } // 剥离原子条件的括号
        if (content.contains("="))
            operator = "=";
        else if (content.contains("<"))
            operator = "<";
        else if (content.contains(">"))
            operator = ">";
        else if (content.equalsIgnoreCase("true"))
            isTrue = true;
        else
            isFalse = true;

        if (!isTrue && !isFalse) {// 忽略true或false条件，因为其中无变量
            variable = content.substring(0, content.indexOf(operator));// 该原子条件变量名
            value = content.substring(content.indexOf(operator) + 1);
            if (variable.charAt(0) == '!') {
                variable = variable.substring(1);
                if (operator.equals("<"))
                    operator = ">=";
                else if (operator.equals(">"))
                    operator = "<=";
                else
                    operator = "!=";
            }
        }
        return isTrue || isFalse;
    }

    /**
     * 找到关键变量和键值
     * 找到原子条件中的关键变量和连续变量的关键值
     * 变量出现在原子条件为关键变量，条件值为关键值
     *
     * @param inputs             输入变量
     * @param tables             中间变量和输出变量
     * @param stateMachines      模式集
     * @param continualVariables 关键连续型变量收集表
     * @param continualRanges    连续型变量值域收集表
     * @param continualValues    连续型变量关键值收集表
     * @param discreteVariables  关键离散型变量收集表
     * @param discreteRanges     离散型变量值域收集表
     */
    //todo: 如果模块化，这个函数是需要重写一个版本的。
    public void findCriticalVariableAndKeyValues(Element inputs, Element tables, Element stateMachines,
                                                 ArrayList<String> continualVariables, ArrayList<ContinualRange> continualRanges, ArrayList<ArrayList<String>> continualValues,
                                                 ArrayList<String> discreteVariables, ArrayList<ArrayList<String>> discreteRanges){
        Iterator inputIterator_T = inputs.elementIterator();
        Iterator tableIterator_T = tables.elementIterator();
        Iterator stateMachineIterator_T = stateMachines.elementIterator();
        while (inputIterator_T.hasNext()) {// 判断变量名是否为输入变量
            Element input_T = (Element) inputIterator_T.next();
            String name = input_T.attributeValue("name");
            if (variable.equals(name)) {// 变量名是输入变量
                if (input_T.element("range").getText().contains("..")) {// 变量连续
                    if (continualVariables.contains(variable)) {// 变量已存储
                        if (!continualValues.get(continualVariables.indexOf(variable))
                                .contains(value)) {// 值未存储
                            ArrayList<String> thisContinualValues = continualValues
                                    .get(continualVariables.indexOf(variable));
                            thisContinualValues.add(value);
                            continualValues.set(continualVariables.indexOf(variable),
                                    thisContinualValues);
                        }
                    } else {// 变量未存储
                        continualVariables.add(variable);
                        ContinualRange cr = new ContinualRange();
                        String[] limits = input_T.element("range").getText().split("\\.\\.");
                        cr.lowLimit = limits[0];
                        cr.highLimit = limits[1];
                        ArrayList<String> thisContinualValues = new ArrayList<String>();
                        thisContinualValues.add(value);
                        continualRanges.add(cr);
                        continualValues.add(thisContinualValues);
                    }
                } else {// 变量离散
                    if (!discreteVariables.contains(variable)) {// 变量未存储
                        discreteVariables.add(variable);
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
            if (variable.equals(name)) {// 变量名是中间、输出变量
                if (table_T.element("range").getText().contains("\\.\\.")) {// 变量连续
                    if (continualVariables.contains(variable)) {// 变量已存储
                        if (!continualValues.get(continualVariables.indexOf(variable))
                                .contains(value)) {// 值未存储
                            ArrayList<String> thisContinualValues = continualValues
                                    .get(continualVariables.indexOf(variable));
                            thisContinualValues.add(value);
                            continualValues.set(continualVariables.indexOf(variable),
                                    thisContinualValues);
                        }
                    } else {// 变量未存储
                        continualVariables.add(variable);
                        ArrayList<String> thisContinualValues = new ArrayList<String>();
                        thisContinualValues.add(value);
                        continualValues.add(thisContinualValues);
                    }
                } else {// 变量离散
                    if (!discreteVariables.contains(variable)) {// 变量未存储
                        discreteVariables.add(variable);
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
            if (variable.equals(name)) {
                if (!discreteVariables.contains(variable)) {// 变量未存储
                    discreteVariables.add(variable);
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
}
