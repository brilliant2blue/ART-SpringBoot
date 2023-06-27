package com.nuaa.art.vrmcheck.model;

import com.nuaa.art.vrm.model.model.VRMOfXML;

import java.util.ArrayList;


public class SubConditionOfEvent extends Condition{
    public ArrayList<ArrayList<Long>> scenariosCode;



    /**
     * 构造方法1，基于条件表每行的条件语句构成的String[]作为参数，被事件转换器调用，替事件分析构造各条件的原子命题树
     * 注意：此构造方法不会生成编码器，也不会生成场景
     * @param vrmModel                  vrm模型
     * @param conditionsForEachRowArray 条件为每一行数组
     */
    public SubConditionOfEvent(VRMOfXML vrmModel, String[] conditionsForEachRowArray) {
        super();
        this.vrmModel = vrmModel;
        this.scenariosCode = new ArrayList<ArrayList<Long>>();
        ArrayList<String> assignmentForEachRow = new ArrayList<String>();
        ArrayList<String> conditionsForEachRow = new ArrayList<String>();
        for (String condition : conditionsForEachRowArray) {
            assignmentForEachRow.add("");
            conditionsForEachRow.add(condition);
        }
        getConditionInformation(conditionsForEachRow, assignmentForEachRow);
    }

    /**
     * 构造方法2，基于事件转换器提供的信息作为参数，被事件转换器调用
     *
     * @param vrmModel           vrm模型
     * @param twoNuclearTrees    一个子事件的两个原子条件树
     * @param continualVariables 整个事件的连续变量
     * @param discreteVariables  整个事件的离散变量
     * @param continualRanges    整个事件的连续变量的值域
     * @param continualValues    整个事件的连续变量的关键值
     * @param discreteRanges     整个事件的离散变量的值域
     * @param coder              整个事件的编码
     */
    public SubConditionOfEvent(VRMOfXML vrmModel, ArrayList<ArrayList<NuclearCondition>>[] twoNuclearTrees,
                               ArrayList<String> continualVariables, ArrayList<String> discreteVariables,
                               ArrayList<ContinualRange> continualRanges, ArrayList<ArrayList<String>> continualValues,
                               ArrayList<ArrayList<String>> discreteRanges, Coder coder) {
        super();
        this.vrmModel = vrmModel;
        this.assignmentForEachRow = new ArrayList<String>();
        this.nuclearTreeForEachRow = new ArrayList<ArrayList<ArrayList<NuclearCondition>>>();
        this.continualVariables = continualVariables;
        this.discreteVariables = discreteVariables;
        this.continualValues = continualValues;
        this.discreteRanges = discreteRanges;
        this.continualRanges = continualRanges;
        this.nuclearTreeForEachRow.add(twoNuclearTrees[0]);
        this.nuclearTreeForEachRow.add(twoNuclearTrees[1]);
        this.coder = coder;
        scenariosCode = new ArrayList<ArrayList<Long>>();
    }

    // 将条件转换为场景集（1.将每个合取式转换为一个未确定所有取值的场景 2.确定场景中的未确定取值，此操作会增加场景的数量）
    public void parseConditionIntoScenarios() {
        if (continualVariables.size() + discreteVariables.size() != 0) {
            buildScenarios();
            for (int i = 0; i < scenariosCode.size(); i++) {
                ArrayList<Long> scenarioCollection = scenariosCode.get(i);
                ArrayList<Long> scenarioCodeCollection;
//            String condition = "";
//            ArrayList<ArrayList<NuclearCondition>> a = nuclearTreeForEachRow.get(i);
//            for (ArrayList<NuclearCondition> b : a) {
//                condition += "(";
//                for (NuclearCondition c : b) {
//                    condition += c.variable + c.operator + c.value;
//                    condition += "&&";
//                }
//                condition = condition.substring(0, condition.length() - 2);
//                condition += ")||";
//            }
//            condition = condition.substring(0, condition.length() - 2);
//            System.out.println("Condition:" + condition);
//            System.out.println("Size:" + scenarioCollection.size());
//            for (Long shit : scenarioCollection) {
//                System.out.println("State:" + shit);
//            }
                scenarioCodeCollection = appendInconsideredToCode(scenarioCollection);
                scenariosCode.set(i, scenarioCodeCollection);
            }
            //outputStates();
        }
    }

    /**
     * 场景分解
     * 确定单条条件场景中的未确定取值，也可用来构建场景全集
     * 前提是，创建场景时变量集要比较完整，即场景分解的变量要全部在内，只是未确定取值。
     * 场景分解的作用则是将这组变量未确定取值的场景分解为一组取到所有值的组合的场景
     *
     * @param scenarioCollection 待分解的场景集合
     * @return {@link ArrayList}<{@link Long}>
     */
    public ArrayList<Long> appendInconsideredToCode(ArrayList<Long> scenarioCollection) {
        ArrayList<Long> scenarioCollectionSingle = new ArrayList<Long>(); //
        for (int j = 0; j < scenarioCollection.size(); j++) {
            long code = scenarioCollection.get(j);
            Scenario thisScenario = coder.decode(code); //解码场景
            //System.out.println("oldState:" + code);
            ArrayList<Integer> inconsidered = new ArrayList<Integer>();
            for (int i = 0; i < thisScenario.scenario.length; i++) {
                if (thisScenario.scenario[i] == 0) {
                    inconsidered.add(i);
                }
            }
            scenarioCollectionSingle = coder.appendInconsidered(scenarioCollectionSingle, code, inconsidered);
        }
        return scenarioCollectionSingle;

    }

    // 输出场景集到Eclipse的Console，调试用
//    public void outputStates() {
//        for (int i = 0; i < scenariosCode.size(); i++) {
//            String condition = "";
//            ArrayList<ArrayList<NuclearCondition>> a = nuclearTreeForEachRow.get(i);
//            for (ArrayList<NuclearCondition> b : a) {
//                condition += "(";
//                for (NuclearCondition c : b) {
//                    condition += c.variable + c.operator + c.value;
//                    condition += "&&";
//                }
//                condition = condition.substring(0, condition.length() - 2);
//                condition += ")||";
//            }
//            condition = condition.substring(0, condition.length() - 2);
//            System.out.println("Condition:" + condition);
//            ArrayList<Long> scenarioCollection = scenariosCode.get(i);
//            String variables = "variables:";
//            for (String variableName : continualVariables) {
//                variables += variableName + ",";
//            }
//            for (String variableName : discreteVariables) {
//                variables += variableName + ",";
//            }
//            variables = variables.substring(0, variables.length() - 1);
//            System.out.println(variables);
//            for (Long thisState : scenarioCollection) {
//                System.out.print(thisState + "\t");
//            }
//            System.out.println();
//        }
//    }

    @Override
    // 将每个合取式转换为一个未确定所有取值的场景
    public void buildScenarios() {
        for (ArrayList<ArrayList<NuclearCondition>> orTree : nuclearTreeForEachRow) {// 遍历每行的析取范式树
            ArrayList<Long> scenarioCollection = new ArrayList<Long>();// 每行的析取范式树新建一个状态集合
            for (ArrayList<NuclearCondition> andTree : orTree) {// 遍历析取范式树的每个合取式
                ArrayList<Long> thisAndTreeScenarios = new ArrayList<Long>();// 对于每个合取式，先新建一个临时列表存储要加入该行条件的状态集合的状态
                ArrayList<Integer>[] valuesForEachVariable = new ArrayList[continualVariables.size()
                        + discreteVariables.size()];
                boolean[] isSetForEachVariable = new boolean[continualVariables.size() + discreteVariables.size()];
                if (andTree.get(0).isTrue) {
                    thisAndTreeScenarios.add(0l);
                    thisAndTreeScenarios = appendInconsideredToCode(thisAndTreeScenarios);
                } else if (andTree.get(0).isFalse) {

                } else {
                    buildAndTreeScenarios(andTree,valuesForEachVariable,isSetForEachVariable);
                    thisAndTreeScenarios.add(0l);
                    for (int i = 0; i < continualVariables.size() + discreteVariables.size(); i++) {
                        if (isSetForEachVariable[i]) {
                            int previousCount = thisAndTreeScenarios.size();
                            for (int j = 0; j < previousCount; j++) {
                                Scenario thisScenario = coder.decode(thisAndTreeScenarios.get(j));
                                boolean isFirstSet = false;
                                for (Integer value : valuesForEachVariable[i]) {
                                    if (!isFirstSet) {
                                        thisScenario.scenario[i] = value.intValue();
                                        isFirstSet = true;
                                        thisAndTreeScenarios.set(j, coder.encode(thisScenario));
                                    } else {
                                        Scenario newScenario = new Scenario(thisScenario);
                                        newScenario.scenario[i] = value.intValue();
                                        thisAndTreeScenarios.add(coder.encode(newScenario));
                                    }
                                }
                            }
                        }
                    }
                }
                for (Long thisState : thisAndTreeScenarios) {
                    if (!scenarioCollection.contains(thisState))
                        scenarioCollection.add(thisState);
                }
            }
            scenariosCode.add(scenarioCollection);
        }
    }
}

