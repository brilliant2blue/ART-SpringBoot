package com.nuaa.art.vrmcheck.service.table;

import com.nuaa.art.vrmcheck.model.scenario.Scenario;
import com.nuaa.art.vrmcheck.model.scenario.ScenarioCorpusCoder;
import com.nuaa.art.vrmcheck.model.table.AndOrConditionsInformation;
import com.nuaa.art.vrmcheck.model.table.AndEventsInformation;
import com.nuaa.art.vrmcheck.model.table.AndOrEventsInformation;
import com.nuaa.art.vrmcheck.model.table.CriticalVariables;

import java.util.ArrayList;

public interface ScenarioHandler {

    /**
     * 构建条件的等价场景集
     * 将条件转换为场景集与行号的映射关系
     * 1.将每个合取式转换为一个或多个含0的场景
     * 2.将每行的行号填入对应的场景索引位置（相同赋值视为一行）
     *
     * @param ci {@link AndOrConditionsInformation} 条件信息
     */
    void buildEquivalentScenarioSet(AndOrConditionsInformation ci);

    /**
     * 构建事件的等价场景集序偶
     * 1.将每个AND事件的两个条件转换为场景集对
     * 2.将场景集对按照事件的逻辑语义进行集合运算，转换为时序场景集对）
     *
     * @param ei {@link AndEventsInformation} 事件信息
     * @return ArrayList<HashSet<Integer>> 事件等价场景集序偶
     */
    void buildEquivalentScenarioSetPair(AndEventsInformation ei);
    /**
     * 构建事件的等价场景集序偶
     * 1.将每个AND事件的两个条件转换为场景集对
     * 2.将场景集对按照事件的逻辑语义进行集合运算，转换为时序场景集对）
     *
     * @param ei {@link AndOrEventsInformation} 事件信息
     * @return ArrayList<HashSet<Integer>> 事件等价场景集序偶
     */
    void buildEquivalentScenarioSetPair(AndOrEventsInformation ei);

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

    ArrayList<String> getScenarioDetails(CriticalVariables c, Scenario s);
}
