package com.nuaa.art.vrmcheck.model.table;


import com.nuaa.art.vrmcheck.model.scenario.ScenarioCorpusCoder;

import java.util.ArrayList;
import java.util.HashSet;

public class AndOrConditionsInformation {
    public ArrayList< // 所有条件
            ArrayList< //每行条件的or树
                    ArrayList< //每个Or树的and条件
                            NuclearCondition
                            >
                    >
            > nuclearTreeForEachRow;// 每个条件的原子命题树
//    public ArrayList<String> continualVariables;// 条件中的数值型变量
//    public ArrayList<String> discreteVariables;// 条件中的枚举型变量
//    public ArrayList<ArrayList<String>> continualRanges; // 每个子list大小为2，表示值域的左右边界
//    public ArrayList<ArrayList<String>> continualValues;// 每个数值型变量在各条件中比较的值
//    public ArrayList<ArrayList<String>> discreteRanges;// 每个枚举型变量的值域

    public CriticalVariables criticalVariables;

    public ArrayList<String> assignmentForEachRow;// 每行的赋值
    public ArrayList<String> outputRanges;// 关联变量的值域
    public ArrayList<HashSet<Integer>> equivalentScenarioSet;// 每个场景对应的输出值号
    public int[] variableRanges;
    public ScenarioCorpusCoder scenarioCorpusCoder;

    public AndOrConditionsInformation() {
        nuclearTreeForEachRow = new ArrayList<>();
//        continualVariables = new ArrayList<>();
//        discreteVariables = new ArrayList<>();
//        continualValues = new ArrayList<>();
//        discreteRanges = new ArrayList<>();
//        continualRanges = new ArrayList<>();
        criticalVariables = new CriticalVariables();
        assignmentForEachRow= new ArrayList<>();
        outputRanges = new ArrayList<>();
        equivalentScenarioSet = new ArrayList<>();
    }
}
