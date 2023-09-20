package com.nuaa.art.vrmcheck.model.obj;

import com.nuaa.art.vrmcheck.model.ScenarioCorpusCoder;

import java.util.ArrayList;

public class EventsInformation {
    public ArrayList<ArrayList<ArrayList<ArrayList<NuclearCondition>>[]>> nuclearTreeForEachRow;// 每个事件的每个子事件的两个条件的原子命题树
    public ArrayList<String> continualVariables;// 条件中的数值型变量
    public ArrayList<String> discreteVariables;// 条件中的枚举型变量
    public ArrayList<ArrayList<String>> continualRanges;
    public ArrayList<ArrayList<String>> eventOperatorsForEachRow;
    public ArrayList<ArrayList<String>> guardOperatorsForEachRow;
    public ArrayList<ArrayList<String>> continualValues;// 每个数值型变量在各条件中比较的值
    public ArrayList<ArrayList<String>> discreteRanges;// 每个枚举型变量的值域
    public ArrayList<String> assignmentForEachRow;// 每行的赋值
    public ArrayList<ArrayList<ArrayList<Long>[]>> twoScenarioSetForEachRow;// 每行事件的每个AND事件的两个条件对应的场景集合
    public ArrayList<ArrayList<Long>[]> scenarioSetPair;// 每行事件对应两个前后场景集合, 等价场景集序偶
    public int[] variableRanges;
    public ScenarioCorpusCoder scenarioCorpusCoder;// 编码器

}