package com.nuaa.art.vrmverify;

import com.nuaa.art.vrm.model.hvrm.HVRM;
import com.nuaa.art.vrm.service.handler.ModelCreateHandler;
import com.nuaa.art.vrmverify.antlr4gen.CTLLexer;
import com.nuaa.art.vrmverify.antlr4gen.CTLParser;
import com.nuaa.art.vrmverify.common.utils.CTLParseUtils;
import com.nuaa.art.vrmverify.common.utils.PathUtils;
import com.nuaa.art.vrmverify.common.utils.TreeTraverseUtils;
import com.nuaa.art.vrmverify.handler.CxExplanationHandler;
import com.nuaa.art.vrmverify.handler.CxVisualizationHandler;
import com.nuaa.art.vrmverify.handler.SmvVerifyHandler;
import com.nuaa.art.vrmverify.handler.Vrm2SmvHandler;
import com.nuaa.art.vrmverify.model.Counterexample;
import com.nuaa.art.vrmverify.model.VerifyResult;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import com.nuaa.art.vrmverify.model.visualization.VariableTable;
import jakarta.annotation.Resource;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author djl
 * @date 2024-03-28
 */
public class Test {

//    public static void main(String[] args) {
//        String expr = "AG (goat=wolf -> wolf=ferrymen)";
//        CTLFormula f = CTLParseUtils.parseCTLStr(expr);
//
//        for (CTLFormula subFormula : CTLFormula.subFormulas) {
//            System.out.println(subFormula.getClass());
//            System.out.println(subFormula);
//        }
//        System.out.println("---------------------------------");
//        TreeTraverseUtils.treeTraverse(f);
//    }

//    public static void main(String[] args) throws Exception {
//        String smvFilePath = PathUtils.getSmvFilePath("guohe.smv");
//        List<String> properties = new ArrayList<>();
//        VerifyResult verifyResult = SmvVerifyHandler.handleVerifyRes(SmvVerifyHandler.doCMD(smvFilePath, true, properties));
//        String cxFilePath = PathUtils.getCxFilePath("cx3");
//        // VerifyResult verifyResult = SmvVerifyHandler.handleVerifyRes(SmvVerifyHandler.getResultFromFile(cxFilePath));
//        System.out.println("--------------------------");
//
//        Counterexample cx = verifyResult.getCxList().get(0);
//        CTLFormula f = CTLParseUtils.parseCTLStr(cx.getProperty());
//         System.out.println(f);
//        VariableTable variableTable = CxVisualizationHandler.computeVariableTable(cx);
//        System.out.println(CxExplanationHandler.computeCauseSet(variableTable, 0, f, false));
//    }

    public static void main(String[] args) {
        String condition = "(iv_Elevator_Valid=2&iv_Flap_Valid=2&iv_MFS_Valid=2&iv_Rudder_Valid=Valid&iv_Horizontal_Stabilizer_Valid=Valid&iv_ACES_Mode_In_FCM_Command=4&iv_Engine_Valid=1&iv_Engine_Valid>1)";
        String event = "{@T((mcAP=mEngage&tv_Any_Vertical_Mode_Engage=False)||(tv_Any_Vertical_Mode_Engage=False&mcLateral_Mode=mROLL)||(mcVertical_Mode=mASEL&!iv_ALT_Knob_Value=0)||(mcVertical_Mode=mALT&iv_Is_PSP_Engage=False)||(!mcAP=mEngage&mcVertical_Mode=mFLC&iv_Is_ASP_Engage=True)||(mcAP=mEngage&mcVertical_Mode=mGS&iv_APPR_Request_Value=True&iv_Is_Autoland_Engage=False)||(mcVertical_Mode=mGS&iv_APPR_Request_Value=True&!mcLateral_Mode=mROLL&iv_Is_Autoland_Engage=False))}||{@T((mcVertical_Mode=mGS&mcAP=mEngage))WHEN((mcLateral_Mode=mLOC))}";
//        String[] split = event.substring(1, event.length() - 1).split("}\\|\\|\\{");
//        for (String s : split) {
//            System.out.println(s);
//        }

        event.replaceAll("\\{", "");
        System.out.println(event);
    }
}
