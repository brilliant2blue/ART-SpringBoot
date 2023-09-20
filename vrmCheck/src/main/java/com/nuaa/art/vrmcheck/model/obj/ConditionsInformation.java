package com.nuaa.art.vrmcheck.model.obj;


import com.nuaa.art.vrmcheck.model.ScenarioCorpusCoder;

import java.util.ArrayList;
import java.util.HashSet;

public class ConditionsInformation {
    public ArrayList<ArrayList<ArrayList<NuclearCondition>>> nuclearTreeForEachRow = new ArrayList<>();// 每个条件的原子命题树
    public ArrayList<String> continualVariables= new ArrayList<>();// 条件中的数值型变量
    public ArrayList<String> discreteVariables= new ArrayList<>();// 条件中的枚举型变量
    public ArrayList<ArrayList<String>> continualRanges= new ArrayList<>(); // 每个子list大小为2，表示值域的左右边界
    public ArrayList<ArrayList<String>> continualValues= new ArrayList<>();// 每个数值型变量在各条件中比较的值
    public ArrayList<ArrayList<String>> discreteRanges= new ArrayList<>();// 每个枚举型变量的值域
    public ArrayList<String> assignmentForEachRow= new ArrayList<>();// 每行的赋值
    public ArrayList<String> outputRanges= new ArrayList<>();// 关联变量的值域
    public ArrayList<HashSet<Integer>> equivalentScenarioSet= new ArrayList<>();// 每个场景对应的输出值号
    public int[] variableRanges;
    public ScenarioCorpusCoder scenarioCorpusCoder;

}
