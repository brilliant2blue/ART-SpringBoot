package com.nuaa.art.vrm.service.handler.impl;

import com.nuaa.art.vrm.model.ConditionItem;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.service.handler.ConditionTableHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件表 通过AndOr表形式创建、显示的实现
 *
 * @author konsin
 * @date 2023/07/02
 */
@Service("conditionForTable")
public class ConditionTableHandlerForTable implements ConditionTableHandler {
    /**
     * 条件AndOr表转换为表达式字符串
     *
     * @param conditionTable 条件表
     * @return {@link String}
     */
    @Override
    public String ConvertTableToString(ConditionTable conditionTable) {
        if (conditionTable.getAndNum() == 0 && conditionTable.getOrNum() == 0) {
            return "true";
        }
        String condition = "";
        ArrayList<String> headList = new ArrayList<String>();
        for (int i = 0; i < conditionTable.getOrNum() + 1; i++) { //为每个or子条件创建条件头
            headList.add("(");
        }

        for (int i = 0; i < conditionTable.getAndNum(); i++) {  // 按行解析and行

            String head = conditionTable.conditionItems.get(i).conditionString(); //获取原始原子条件

            for (int j = 0; j < conditionTable.getOrNum(); j++) {   //按列解析or列
                if (conditionTable.orList.get(i).get(j).equals("T")) {
                    String string = headList.get(j);
                    if (!headList.get(j).equals("(")) {
                        string = string + "&";
                    }
                    string += head;
                    headList.set(j, string);
                } else if (conditionTable.orList.get(i).get(j).equals("F")) {
                    String string = headList.get(j);
                    if (!headList.get(j).equals("(")) {
                        string = string + "&";
                    }
                    string += "!" + head;
                    headList.set(j, string);
                } else if (conditionTable.orList.get(i).get(j).equals(".")) {

                }
            }
        }
        for (int i = 0; i < conditionTable.getOrNum() + 1; i++) {  //排除空结果
            if (!headList.get(i).equals("()")) {
                headList.set(i, headList.get(i) + ")");
            } else {
                headList.set(i, "");
            }
        }

        for (String s : headList) {
            if (headList.indexOf(s) != headList.size() - 1) {
                condition += s + "||";
            }
        }

        condition = condition.substring(0, condition.length() - 2);
//        System.out.println(condition);

        if (condition.contains("()||()")) {
            condition = "true";
        }
        return condition;
    }

    /**
     * 将表达式字符串转换成条件表
     *
     * @param condition 条件
     * @return {@link ConditionTable}
     */
    @Override
    public ConditionTable ConvertStringToTable(String condition) {
        ConditionTable conditionTable = new ConditionTable();
        //System.out.println("完整的条件语句:   " + condition);
        if (condition.equalsIgnoreCase("true") || condition.equalsIgnoreCase("")) {
            return conditionTable;
        }

        ArrayList<String> nuclearConditions = new ArrayList<String>(); //存储解析的原子条件字符串

        String[] orContents = condition.split("\\|\\|"); //划分析取范式

        conditionTable.setOrNum(orContents.length);
        //System.out.println(orContents.length);

        for (int col = 0; col < conditionTable.getOrNum(); col++) { //处理合取式
            String s = orContents[col];
            List<Integer> trueIntegers = new ArrayList<Integer>(); //暂存为真的原子条件 行号
            List<Integer> falseIntegers = new ArrayList<Integer>(); //暂存为假的原子条件 行号

            s = s.substring(0, s.length());
            //System.out.println("OR:   " + s);
            String[] andContents = s.split("&"); //划分合取式

            for (String s1 : andContents) {
                String nuclearCondition = s1.replaceAll("\\(", "");
                nuclearCondition = nuclearCondition.replaceAll("\\)", "");
                //System.out.println("AND:   " + s1);
                boolean nuclearConditionTrue = true;

                if (nuclearCondition.contains("!")) {
                    nuclearConditionTrue = false;
                    nuclearCondition = nuclearCondition.replaceAll("!", "");
                }

                if (!nuclearConditions.contains(nuclearCondition)) { //获取原子条件的各个组成部分
                    ArrayList<String> orList = new ArrayList<>(conditionTable.getOrNum()); //新建or行
                    for (int i = 0; i < conditionTable.getOrNum(); i++) {    //初始化orList行
                        orList.add(".");
                    }

                    boolean moreOper = s1.contains(">");
                    boolean equalOper = s1.contains("=");
                    boolean lessOper = s1.contains("<");
                    boolean isNotCondition = false;
                    String left = "";
                    String right = "";
                    String operator = "";
                    int operIndex = 0;
                    if (moreOper == true) {
                        operIndex = s1.indexOf(">");
                        operator = ">";
                    }

                    if (equalOper == true) {
                        operIndex = s1.indexOf("=");
                        operator = "=";
                    }

                    if (lessOper == true) {
                        operIndex = s1.indexOf("<");
                        operator = "<";
                    }

                    if (moreOper == false && lessOper == false && equalOper == false) {
                        String temp = s1.replaceAll("\\(", "");
                        temp = temp.replaceAll("\\)", "");

                        left = temp;
                        right = "";
                    } else {

                        left = s1.substring(0, operIndex).trim();
                        right = s1.substring(operIndex + 1).trim();

                        //处理左值
                        left = left.replaceAll("\\(", "");
                        left = left.replaceAll("\\)", "");

                        if (left.charAt(0) == '!') {
                            left = left.substring(1);
                            isNotCondition = true;
                        }

                        right = right.replaceAll("\\(", "");
                        right = right.replaceAll("\\)", "");
                    }

                    // 添加新条件
                    nuclearConditions.add(nuclearCondition);
                    ConditionItem newConditionItem = new ConditionItem(left, operator, right);
                    conditionTable.getConditionItems().add(newConditionItem);
                    conditionTable.getOrList().add(orList); //添加新的空or行
                    conditionTable.addAndNum(); //变量数增加

                    if (isNotCondition)
                        falseIntegers.add(conditionTable.getAndNum());
                    else
                        trueIntegers.add(conditionTable.getAndNum());

                } else {
                    if (!nuclearConditionTrue)
                        falseIntegers.add(nuclearConditions.indexOf(nuclearCondition) + 1);
                    else
                        trueIntegers.add(nuclearConditions.indexOf(nuclearCondition) + 1);
                }
            }
            // 填充or列
            for (int i = 0; i < conditionTable.getAndNum(); i++) {
                for (Integer integer : trueIntegers) {
                    if (integer - 1 == i) {
                        conditionTable.getOrList().get(i).set(col, "T");
                    }
                }
                for (Integer integer : falseIntegers) {
                    if (integer - 1 == i) {
                        conditionTable.getOrList().get(i).set(col, "F");
                    }
                }
            }
        }
        return conditionTable;
    }
}
