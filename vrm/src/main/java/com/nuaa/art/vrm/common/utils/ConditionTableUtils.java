package com.nuaa.art.vrm.common.utils;

import com.nuaa.art.common.utils.LogUtils;
import com.nuaa.art.vrm.model.ConditionItem;
import com.nuaa.art.vrm.model.ConditionTable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 条件表 通过AndOr表形式创建、显示的实现
 *
 * @author konsin
 * @date 2023/07/02
 */
@Service("conditionForTable")
public class ConditionTableUtils {
    /**
     * 条件AndOr表转换为表达式字符串
     *
     * @param conditionTable 条件表
     * @return {@link String}
     */

    public String ConvertTableToString(ConditionTable conditionTable) {
        /* 判断条件是否为永真永假
         * 永真则表有一个原子条件和Or列，但是原子条件为空，OR为 T
         * 永假则有一个原子条件和Or列，但是原子条件为空，OR为 F
         * 其余情况返回空值，即默认
         */
        if (conditionTable.getAndNum() == 0) {
            return "default";
        } else if(conditionTable.getAndNum() == 1 && conditionTable.getOrNum() == 1 && conditionTable.getConditionItems().get(0).whetherEmpty()){
            String OR = conditionTable.getOrList().get(0).get(0);
            if(OR.equals(".")) return "default";
            else if(OR.equals("T")) return "true";
            else return "false";
        }
        String condition = "";
        ArrayList<String> headList = new ArrayList<String>();
        for (int i = 0; i < conditionTable.getOrNum(); i++) { //为每个or子条件创建条件头
            headList.add("(");
        }

        for (int i = 0; i < conditionTable.getAndNum(); i++) {  // 按行解析and行

            String head = conditionTable.conditionItems.get(i).conditionString();

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
        int emptyCount = 0;
        for (int i = 0; i < conditionTable.getOrNum(); i++) {  //排除空结果
            if (!headList.get(i).equals("(")) {
                headList.set(i, headList.get(i) + ")");
            } else {
                headList.set(i, "");
                emptyCount++;
            }
        }

        if (emptyCount == conditionTable.getOrNum()) { //设置了条件，但是所有的条件都取值任意，则认为是永真式
            condition = "true";
        } else {
            condition = headList.stream().filter((item)->!item.isBlank()).collect(Collectors.joining("||"));
            //System.out.println(condition);
        }
        return condition;
    }

    /**
     * 将表达式字符串转换成条件表
     *
     * @param condition 条件
     * @return {@link ConditionTable}
     */

    public ConditionTable ConvertStringToTable(String condition) {
        ConditionTable conditionTable = new ConditionTable();
        System.out.println("完整的条件语句:   " + condition);
        if (condition.equalsIgnoreCase("") || condition.equalsIgnoreCase("default")) {
            conditionTable.getConditionItems().add(new ConditionItem());
            conditionTable.addAndNum();
            conditionTable.setOrNum(1);
            ArrayList<String> orList = new ArrayList<>();
            orList.add(".");
            conditionTable.getOrList().add(orList);
            return conditionTable;
        }
        else if (condition.equalsIgnoreCase("true")) {
            conditionTable.getConditionItems().add(new ConditionItem());
            conditionTable.addAndNum();
            conditionTable.setOrNum(1);
            ArrayList<String> orList = new ArrayList<>();
            orList.add("T");
            conditionTable.getOrList().add(orList);
            return conditionTable;
        } else if (condition.equalsIgnoreCase("false")) {
            conditionTable.getConditionItems().add(new ConditionItem());
            conditionTable.addAndNum();
            conditionTable.setOrNum(1);
            ArrayList<String> orList = new ArrayList<>();
            orList.add("F");
            conditionTable.getOrList().add(orList);
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
                    // 修复 字符转换的bug
                    boolean moreOper = nuclearCondition.contains(">");
                    boolean equalOper = nuclearCondition.contains("=");
                    boolean lessOper = nuclearCondition.contains("<");
                    boolean isNotCondition = false;
                    String left = "";
                    String right = "";
                    String operator = "";
                    int operIndex = 0;
                    if (moreOper == true) {
                        operIndex = nuclearCondition.indexOf(">");
                        operator = ">";
                    }

                    if (equalOper == true) {
                        operIndex = nuclearCondition.indexOf("=");
                        operator = "=";
                    }

                    if (lessOper == true) {
                        operIndex = nuclearCondition.indexOf("<");
                        operator = "<";
                    }

                    if (moreOper == false && lessOper == false && equalOper == false) {
                        String temp = nuclearCondition.replaceAll("\\(", "");
                        temp = temp.replaceAll("\\)", "");

                        left = temp;
                        right = "";
                    } else {

                        left = nuclearCondition.substring(0, operIndex).trim();
                        right = nuclearCondition.substring(operIndex + 1).trim();

                        //处理左值
                        left = left.replaceAll("\\(", "");
                        left = left.replaceAll("\\)", "");

                        right = right.replaceAll("\\(", "");
                        right = right.replaceAll("\\)", "");
                    }

                    // 添加新条件
                    nuclearConditions.add(nuclearCondition);
                    ConditionItem newConditionItem = new ConditionItem(left, operator, right);
                    conditionTable.getConditionItems().add(newConditionItem);
                    conditionTable.getOrList().add(orList); //添加新的空or行
                    conditionTable.addAndNum(); //变量数增加

                    if (!nuclearConditionTrue)
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
