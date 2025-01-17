
package com.nuaa.art.vrmcheck;

import com.nuaa.art.main.MainApplication;
import com.nuaa.art.vrm.common.utils.ConditionTableUtils;
import com.nuaa.art.vrm.model.ConditionItem;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.model.vrm.TableOfVRM;
import com.nuaa.art.vrm.model.vrm.TableRow;
import com.nuaa.art.vrm.model.vrm.VRM;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmcheck.model.repoter.CheckErrorReporter;
import com.nuaa.art.vrmcheck.model.scenario.Scenario;
import com.nuaa.art.vrmcheck.model.table.AndOrConditionsInformation;
import com.nuaa.art.vrmcheck.model.table.CriticalVariables;
import com.nuaa.art.vrmcheck.model.table.NuclearCondition;
import com.nuaa.art.vrmcheck.service.table.ConditionCheck;
import com.nuaa.art.vrmcheck.service.table.ConditionParser;
import com.nuaa.art.vrmcheck.service.table.EventCheck;
import com.nuaa.art.vrmcheck.service.table.ScenarioHandler;
import com.nuaa.art.vrmcheck.service.table.impl.AndOrConditionParserImpl;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = MainApplication.class)
public class testConditionInfo {
    @Resource
    ConditionTableUtils tableUtils;
//    @Test
//    public void test(){
//
//        String con = "(!ipDU1Fail=True&!ipDU2Fail=True&!ipDU3Fail=True&!ipDU4Fail=True&!ipDU5Fail=True)";
//        System.out.println(con);
//        ConditionTable thisCondition = tableUtils.ConvertStringToTable(con);
//        ArrayList<ArrayList<NuclearCondition>> orTree = new ArrayList<>();
//        ArrayList<ConditionItem> conditions = thisCondition.getConditionItems(); //原始的原子条件元素
//        ArrayList<ArrayList<String>> orRelation = thisCondition.getOrList(); // 每个orlist是每个原子条件在or树中的取值正反情况
//        for (int i = 0; i < thisCondition.getOrNum(); i++) { // 遍历获取一组and条件
//            ArrayList<NuclearCondition> andTree = new ArrayList<>();
//            for(int j = 0; j < thisCondition.getAndNum(); j++) {
//                if(orRelation.get(j).get(i).equals("T")){
//                    if(conditions.get(j).whetherEmpty()){
//                        NuclearCondition nc = new NuclearCondition();
//                        nc.setTrue();
//                        andTree.add(nc);
//                        System.out.println("永真");
//                    } else {
//                        NuclearCondition nc = new NuclearCondition(conditions.get(j));
//                        andTree.add(nc);
//                        System.out.println("真");
//                    }
//                } else if (orRelation.get(j).get(i).equals("F")) {
//                    if(conditions.get(j).whetherEmpty()){
//                        NuclearCondition nc = new NuclearCondition();
//                        nc.setFalse();
//                        andTree.add(nc);
//                        System.out.println("永假");
//                    } else {
//                        NuclearCondition nc = new NuclearCondition(conditions.get(j));
//                        String operator = nc.getOperator();
//                        if (operator.equals("<"))
//                            operator = ">=";
//                        else if (operator.equals(">"))
//                            operator = "<=";
//                        else
//                            operator = "!=";
//                        nc.setOperator(operator);
//                        andTree.add(nc);
//                        System.out.println("假");
//                    }
//                }
//            }
//            orTree.add(andTree);
//        }
//    }

    @Resource(name = "vrm-object")
    ModelCreateHandler modelObjectCreate;

//    @Resource
//    ConditionCheck conditionCheck;

//    @Test
//    public void testConditionCheck(){
//        HVRM vrm = (HVRM) modelObjectCreate.modelFile(null, "D:\\CodePath\\ART\\ART-SpringBoot\\cache\\fgcsmmodel.xml");
//        CheckErrorReporter checkErrorReporter = new CheckErrorReporter();
//        conditionCheck.checkConditionIntegrityAndConsistency(vrm.convertToVRM(), checkErrorReporter);
//
//    }

    @Resource
    ConditionParser conditionPraser;
    @Resource(name="ConditionCheckV2")
    ConditionCheck conditionCheck;
    @Test
    public void testConditionScenario(){
        HVRM vrmModel = (HVRM) modelObjectCreate.modelFile(null, "D:\\CodePath\\ART\\ART-SpringBoot\\cache\\condition_test.xml");

        CheckErrorReporter checkErrorReporter = new CheckErrorReporter();
        conditionCheck.checkConditionIntegrityAndConsistency(vrmModel.convertToVRM(), checkErrorReporter);

    }

//    @Resource
//    EventCheck eventCheck;
//    @Test
//    public void testModeTransCheck(){
//        VRM vrm = (VRM) modelObjectCreate.modelFile(null, "D:\\CodePath\\ART\\ART-SpringBoot\\cache\\testAP.xml");
//        CheckErrorReporter checkErrorReporter = new CheckErrorReporter();
//        eventCheck.checkModeTransConsistency(vrm, checkErrorReporter);
//        System.out.println(checkErrorReporter.getErrorList().toString());
//    }

    public ArrayList<Scenario> buildAndTreeEquivalentScenarioSet(AndOrConditionsInformation ci, ArrayList<NuclearCondition> andTree){
        int keyVariableSize = ci.criticalVariables.size();
        CriticalVariables cv = ci.criticalVariables;
        ArrayList<Scenario> thisAndTreeScenarios = new ArrayList<>();// 对于每个合取式，先新建一个临时列表存储要加入该行条件的状态集合的状态
        ArrayList<Integer>[] valuesForEachVariable = new ArrayList[keyVariableSize];
        boolean[] isSetForEachVariable = new boolean[keyVariableSize];
        for (NuclearCondition nuclear : andTree) {// 遍历合取式的每个原子条件
            ArrayList<Integer> thisVariableValues = new ArrayList<Integer>();
            int variableIndexInState;
            if (cv.continualVariables.contains(nuclear.getVar1())) {// 该原子条件为连续型变量
                int variableIndex = cv.continualVariables.indexOf(nuclear.getVar1());// 得到变量在连续变量集合里的索引值
                variableIndexInState = variableIndex;
                if (nuclear.getOperator().equals("=")) {// 等于变量对应的取值集合中第n个值，等价于变量等于离散值2n
                    thisVariableValues.add(cv.continualValues.get(variableIndex).indexOf(nuclear.getVar2()) * 2 + 2);
                } else if (nuclear.getOperator().equals("<")) {// 小于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n-1
                    for (int value = 1; value < cv.continualValues.get(variableIndex).indexOf(nuclear.getVar2())
                            * 2 + 2; value++) {
                        thisVariableValues.add(value);
                    }
                } else if (nuclear.getOperator().equals(">")) {// 大于变量对应的取值集合中第n个值，等价于变量等于离散值2n+1..max
                    for (int value = cv.continualValues.get(variableIndex).indexOf(nuclear.getVar2()) * 2
                            + 3; value < cv.continualValues.get(variableIndex).size() * 2 + 2; value++) {
                        thisVariableValues.add(value);
                    }
                } else if (nuclear.getOperator().equals("<=")) {// 小于等于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n
                    for (int value = 1; value <= cv.continualValues.get(variableIndex).indexOf(nuclear.getVar2())
                            * 2 + 2; value++) {
                        thisVariableValues.add(value);
                    }
                } else if (nuclear.getOperator().equals(">=")) {// 大于等于变量对应的取值集合中第n个值，等价于变量等于离散值2n..max
                    for (int value = cv.continualValues.get(variableIndex).indexOf(nuclear.getVar2()) * 2
                            + 2; value < cv.continualValues.get(variableIndex).size() * 2 + 2; value++) {
                        thisVariableValues.add(value);
                    }
                } else {// 不等于变量对应的取值集合中第n个值，等价于变量等于离散值1..2n-1&2n+1..max
                    for (int value = 1; value < cv.continualValues.get(variableIndex).size() * 2
                            + 2; value++) {
                        if (value != cv.continualValues.get(variableIndex).indexOf(nuclear.getVar2()) * 2 + 2)
                            thisVariableValues.add(value);
                    }
                }
            } else {// 该原子条件为离散型变量
                int variableIndex = cv.discreteVariables.indexOf(nuclear.getVar1());// 得到变量在离散变量集合里的索引值
                variableIndexInState = variableIndex + cv.continualVariables.size();// 变量在状态中的索引值，因为连续变量和离散变量共同组成状态，所以该索引值应该在离散变量索引的基础上加上连续变量的个数
                if (nuclear.getOperator().equals("=")) {// 等于变量对应的取值集合中第n个值，等价于变量等于离散值n
                    thisVariableValues.add(
                            cv.discreteRanges.get(variableIndex).indexOf(nuclear.getVar2()) + 1);
                } else {// 不等于变量对应的取值集合中第n个值，等价于变量等于离散值1..n-1&n+1..max
                    for (int value = 1; value < cv.discreteRanges.get(variableIndex).size() + 1; value++) {
                        if (value != cv.discreteRanges.get(variableIndex).indexOf(nuclear.getVar2()) + 1)
                            thisVariableValues.add(value);
                    }
                }
            }
            if (!isSetForEachVariable[variableIndexInState]) {
                valuesForEachVariable[variableIndexInState] = thisVariableValues;
            } else {
                valuesForEachVariable[variableIndexInState].retainAll(thisVariableValues);
            }
            isSetForEachVariable[variableIndexInState] = true;
        }

        thisAndTreeScenarios.add(new Scenario(keyVariableSize));
        for (int i = 0; i < keyVariableSize; i++) {
            if (isSetForEachVariable[i]) {
                int previousCount = thisAndTreeScenarios.size();
                for (int j = 0; j < previousCount; j++) {
                    Scenario thisScenario = thisAndTreeScenarios.get(j);
                    boolean isFirstSet = false;
                    for (Integer value : valuesForEachVariable[i]) {
                        if (!isFirstSet) {
                            thisScenario.scenario[i] = value;
                            isFirstSet = true;
                            thisAndTreeScenarios.set(j, thisScenario);
                        } else {
                            Scenario newScenario = new Scenario(thisScenario);
                            newScenario.scenario[i] = value;
                            thisAndTreeScenarios.add(newScenario);
                        }
                    }
                }
            }
        }

        return thisAndTreeScenarios;
    }

}
