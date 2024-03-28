package com.nuaa.art.vrmverify;

import com.nuaa.art.vrmverify.antlr4gen.CTLLexer;
import com.nuaa.art.vrmverify.antlr4gen.CTLParser;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author djl
 * @date 2024-03-28
 */
public class Test {

    public static void main(String[] args) {
        String expr = "AG (goat=wolf -> wolf=ferrymen)";
        // 对每一个输入的字符串，构造一个 ANTLRStringStream 流 in
        ANTLRInputStream input = new ANTLRInputStream(expr);
        // 用 in 构造词法分析器 lexer，词法分析的作用是将字符聚集成单词或者符号
        CTLLexer lexer = new CTLLexer(input);
        // 用词法分析器 lexer 构造一个记号流 tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 再使用 tokens 构造语法分析器 parser,至此已经完成词法分析和语法分析的准备工作
        CTLParser parser = new CTLParser(tokens);
        // 最终调用语法分析器的规则 parse（在demo.g4里定义的规则），完成对表达式的验证
        CTLFormula f = parser.formula_eof().f;

        for (CTLFormula subFormula : CTLFormula.subFormulas) {
            System.out.println(subFormula.getClass());
            System.out.println(subFormula);
        }
    }
}
