package com.nuaa.art.vrmcheck.service.obj;

import com.nuaa.art.vrmcheck.model.Scenario;
import com.nuaa.art.vrmcheck.model.ScenarioCorpusCoder;
import com.nuaa.art.vrmcheck.model.obj.ConditionsInformation;
import com.nuaa.art.vrmcheck.model.obj.EventsInformation;

import java.util.ArrayList;
import java.util.HashSet;

public interface ScenarioHandler {

    /**
     * 构建条件的等价场景集
     * 将条件转换为场景集与行号的映射关系
     * 1.将每个合取式转换为一个或多个含0的场景
     * 2.将每行的行号填入对应的场景索引位置（相同赋值视为一行）
     *
     * @param ci {@link ConditionsInformation} 条件信息
     * @return ArrayList<HashSet<Integer>> 条件等价场景集
     */
    ArrayList<HashSet<Integer>> buildEquivalentScenarioSet(ConditionsInformation ci);

    /**
     * 构建事件的等价场景集序偶
     * 1.将每个AND事件的两个条件转换为场景集对
     * 2.将场景集对按照事件的逻辑语义进行集合运算，转换为时序场景集对）
     *
     * @param ei {@link ConditionsInformation} 事件信息
     * @return ArrayList<HashSet<Integer>> 事件等价场景集序偶
     */
    void buildEquivalentScenarioSetPair(EventsInformation ei);

    /**
     * 构建场景全集
     * 场景全集表现为一个编码解码器，每一个编码对应一个场景
     *
     * @param varNum    变量数量。
     * @param varRanges 变量值域
     * @return {@link ScenarioCorpusCoder}
     */
    ScenarioCorpusCoder constructScenarioCorpus(int varNum, int[] varRanges);

    /**
     * 场景分解
     * 确定单条条件场景中的未确定取值，也可用来构建场景全集
     * 前提是，创建场景时变量集要比较完整，即场景分解的变量要全部在内，只是未确定取值。
     * 场景分解的作用则是将这组变量未确定取值的场景分解为一组取到所有值的组合的场景
     *
     * @param scenarioCollection 待分解的场景集合
     * @param coder              场景全集
     * @return {@link ArrayList}<{@link Long}>
     */
    ArrayList<Long> scenarioResolving(ArrayList<Long> scenarioCollection, ScenarioCorpusCoder coder);

    <T> ArrayList<String> getScenarioDetails(T c, Scenario s);
}
