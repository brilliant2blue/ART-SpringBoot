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
import com.nuaa.art.vrmverify.service.impl.Vrm2SmvServiceImpl;
import jakarta.annotation.Resource;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

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
        System.out.println(CTLParseUtils.parseCTLStr("!E [((goat=wolf -> goat=ferrymen) & (goat=cabbage -> goat=ferrymen)) U (cabbage =TRUE & goat=TRUE & wolf=TRUE & ferrymen=TRUE)]").variableSet());

    }

}
