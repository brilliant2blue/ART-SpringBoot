package com.nuaa.art.vrmcheck.model.table;

import com.nuaa.art.vrmcheck.model.scenario.ScenarioCorpusCoder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * AndOr表形式的事件信息
 *
 * @author konsin
 * @date 2023/12/07
 */
public class AndOrEventsInformation {
    public ArrayList< //事件树集合
            ArrayList< //每行事件的or事件树
                ArrayList< // 每行事件的and事件树
                        CoreEvent
                        >
                >
            >nuclearTreeForEachRow;// 每个事件的每个子事件的两个条件的原子命题树

//    public ArrayList<String> continualVariables;// 条件中的数值型变量
//    public ArrayList<String> discreteVariables;// 条件中的枚举型变量
//    public ArrayList<ArrayList<String>> continualRanges;
//
//    public ArrayList<ArrayList<String>> continualValues;// 每个数值型变量在各条件中比较的值
//    public ArrayList<ArrayList<String>> discreteRanges;// 每个枚举型变量的值域

    public CriticalVariables criticalVariables;

    public ArrayList<String> assignmentForEachRow;// 每行的赋值

    public int[] variableRanges;
    public ScenarioCorpusCoder scenarioCorpusCoder;// 编码器

    // 以下的属性中，行概念是按相同赋值来进行划分的。

    public ArrayList<String> outputRanges;// 每行对应变量的值域

    public ArrayList<Integer> rowsForTrueScenarioSet; //永真行
    public ArrayList<Integer> rowsForDefaultScenarioSet; //默认行

    // 每行事件对应等价场景集序偶(两个前后场景集合)的集合
    public HashMap<Integer, ArrayList<ArrayList<Long>[]>> scenarioSetPairsOfEachRow;



    public AndOrEventsInformation() {
        nuclearTreeForEachRow = new ArrayList<>();
//        continualVariables = new ArrayList<String>();
//        discreteVariables = new ArrayList<String>();
//        continualValues = new ArrayList<ArrayList<String>>();
//        discreteRanges = new ArrayList<ArrayList<String>>();
//        continualRanges = new ArrayList<ArrayList<String>>();
        criticalVariables = new CriticalVariables();
        assignmentForEachRow = new ArrayList<>();
        scenarioSetPairsOfEachRow = new HashMap<>();
        nuclearTreeForEachRow = new ArrayList<>();
    }
}
