package com.nuaa.art.vrmverify.common.utils;

import com.nuaa.art.vrmverify.antlr4gen.CTLLexer;
import com.nuaa.art.vrmverify.antlr4gen.CTLParser;
import com.nuaa.art.vrmverify.common.exception.InvalidCTLException;
import com.nuaa.art.vrmverify.model.formula.ctl.CTLFormula;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

/**
 * CTL语法解析工具
 * @author djl
 * @date 2024-04-01
 */
public class CTLParseUtils {

    public static CTLFormula parseCTLStr(String cltStr) throws InvalidCTLException {
        return getCTLFormulaFromString(cltStr);
    }

    public static CTLFormula parseCTLStrAndSimplify(String ctlStr) throws InvalidCTLException {
        CTLFormula f = getCTLFormulaFromString(ctlStr);
        if(f != null)
            return f.removeImplication().removeEquivalence().removeXor().toNNF();
        else
            return null;
    }

    /**
     * 从字符串解析得CTL公式
     * @return 若解析失败，返回 null
     */
    private static CTLFormula getCTLFormulaFromString(String ctlStr){
        if(ctlStr == null) ctlStr = "";
        // 对每一个输入的字符串，构造一个 ANTLRStringStream 流 in
        ANTLRInputStream input = new ANTLRInputStream(ctlStr);
        // 用 in 构造词法分析器 lexer，词法分析的作用是将字符聚集成单词或者符号
        CTLLexer lexer = new CTLLexer(input);
        // 用词法分析器 lexer 构造一个记号流 tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 使用 tokens 构造语法分析器 parser,至此已经完成词法分析和语法分析的准备工作
        CTLParser parser = new CTLParser(tokens);
        // 最终调用语法分析器的规则 parse（在demo.g4里定义的规则），完成对表达式的验证
        try {
            return parser.formula_eof().f;
        }
        catch (RecognitionException re) {
            throw new InvalidCTLException(ctlStr);
        }
    }

}
