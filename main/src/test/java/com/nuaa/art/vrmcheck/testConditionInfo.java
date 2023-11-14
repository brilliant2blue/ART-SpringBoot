
package com.nuaa.art.vrmcheck;

import com.nuaa.art.main.MainApplication;
import com.nuaa.art.vrm.common.utils.ConditionTableUtils;
import com.nuaa.art.vrm.model.ConditionItem;
import com.nuaa.art.vrm.model.ConditionTable;
import com.nuaa.art.vrm.model.VariableRealationModel;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmcheck.model.CheckErrorReporter;
import com.nuaa.art.vrmcheck.model.obj.NuclearCondition;
import com.nuaa.art.vrmcheck.service.obj.ConditionCheck;
import com.nuaa.art.vrmcheck.service.obj.EventCheck;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest(classes = MainApplication.class)
public class testConditionInfo {
    @Resource
    ConditionTableUtils tableUtils;
    @Test
    public void test(){

        String con = "(!ipDU1Fail=True&!ipDU2Fail=True&!ipDU3Fail=True&!ipDU4Fail=True&!ipDU5Fail=True)";
        System.out.println(con);
        ConditionTable thisCondition = tableUtils.ConvertStringToTable(con);
        ArrayList<ArrayList<NuclearCondition>> orTree = new ArrayList<>();
        ArrayList<ConditionItem> conditions = thisCondition.getConditionItems(); //原始的原子条件元素
        ArrayList<ArrayList<String>> orRelation = thisCondition.getOrList(); // 每个orlist是每个原子条件在or树中的取值正反情况
        for (int i = 0; i < thisCondition.getOrNum(); i++) { // 遍历获取一组and条件
            ArrayList<NuclearCondition> andTree = new ArrayList<>();
            for(int j = 0; j < thisCondition.getAndNum(); j++) {
                if(orRelation.get(j).get(i).equals("T")){
                    if(conditions.get(j).whetherEmpty()){
                        NuclearCondition nc = new NuclearCondition();
                        nc.setTrue();
                        andTree.add(nc);
                        System.out.println("永真");
                    } else {
                        NuclearCondition nc = new NuclearCondition(conditions.get(j));
                        andTree.add(nc);
                        System.out.println("真");
                    }
                } else if (orRelation.get(j).get(i).equals("F")) {
                    if(conditions.get(j).whetherEmpty()){
                        NuclearCondition nc = new NuclearCondition();
                        nc.setFalse();
                        andTree.add(nc);
                        System.out.println("永假");
                    } else {
                        NuclearCondition nc = new NuclearCondition(conditions.get(j));
                        String operator = nc.getOperator();
                        if (operator.equals("<"))
                            operator = ">=";
                        else if (operator.equals(">"))
                            operator = "<=";
                        else
                            operator = "!=";
                        nc.setOperator(operator);
                        andTree.add(nc);
                        System.out.println("假");
                    }
                }
            }
            orTree.add(andTree);
        }
    }

    @Resource(name = "vrm-object")
    ModelCreateHandler modelObjectCreate;

    @Resource
    ConditionCheck conditionCheck;

    @Test
    public void testConditionCheck(){
        VariableRealationModel vrm = (VariableRealationModel) modelObjectCreate.modelFile(null, "D:\\CodePath\\ART\\ART-SpringBoot\\cache\\test3model.xml");
        CheckErrorReporter checkErrorReporter = new CheckErrorReporter();
        conditionCheck.checkConditionIntegrityAndConsistency(vrm, checkErrorReporter);

    }

    @Resource
    EventCheck eventCheck;
    @Test
    public void testModeTransCheck(){
        VariableRealationModel vrm = (VariableRealationModel) modelObjectCreate.modelFile(null, "D:\\CodePath\\ART\\ART-SpringBoot\\cache\\testAP.xml");
        CheckErrorReporter checkErrorReporter = new CheckErrorReporter();
        eventCheck.checkModeTransConsistency(vrm, checkErrorReporter);
        System.out.println(checkErrorReporter.getErrorList().toString());
    }

}
