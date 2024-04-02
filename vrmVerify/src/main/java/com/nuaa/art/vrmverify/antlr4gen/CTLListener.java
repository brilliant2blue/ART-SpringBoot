// Generated from E:/idea_projects/ART-SpringBoot/vrmVerify/src/main/resources/CTL.g4 by ANTLR 4.13.1
package com.nuaa.art.vrmverify.antlr4gen;

    import com.nuaa.art.vrmverify.model.formula.expression.*;
    import com.nuaa.art.vrmverify.model.formula.ctl.*;
    import com.nuaa.art.vrmverify.model.formula.TreeNode;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CTLParser}.
 */
public interface CTLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CTLParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(CTLParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(CTLParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#composite_id}.
	 * @param ctx the parse tree
	 */
	void enterComposite_id(CTLParser.Composite_idContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#composite_id}.
	 * @param ctx the parse tree
	 */
	void exitComposite_id(CTLParser.Composite_idContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#path_quantifier_sign}.
	 * @param ctx the parse tree
	 */
	void enterPath_quantifier_sign(CTLParser.Path_quantifier_signContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#path_quantifier_sign}.
	 * @param ctx the parse tree
	 */
	void exitPath_quantifier_sign(CTLParser.Path_quantifier_signContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#unary_operator_sign}.
	 * @param ctx the parse tree
	 */
	void enterUnary_operator_sign(CTLParser.Unary_operator_signContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#unary_operator_sign}.
	 * @param ctx the parse tree
	 */
	void exitUnary_operator_sign(CTLParser.Unary_operator_signContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator_sign5}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator_sign5(CTLParser.Binary_operator_sign5Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator_sign5}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator_sign5(CTLParser.Binary_operator_sign5Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator_sign4}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator_sign4(CTLParser.Binary_operator_sign4Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator_sign4}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator_sign4(CTLParser.Binary_operator_sign4Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator_sign3}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator_sign3(CTLParser.Binary_operator_sign3Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator_sign3}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator_sign3(CTLParser.Binary_operator_sign3Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator_sign2}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator_sign2(CTLParser.Binary_operator_sign2Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator_sign2}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator_sign2(CTLParser.Binary_operator_sign2Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator_sign1}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator_sign1(CTLParser.Binary_operator_sign1Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator_sign1}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator_sign1(CTLParser.Binary_operator_sign1Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#comparison_operator_sign}.
	 * @param ctx the parse tree
	 */
	void enterComparison_operator_sign(CTLParser.Comparison_operator_signContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#comparison_operator_sign}.
	 * @param ctx the parse tree
	 */
	void exitComparison_operator_sign(CTLParser.Comparison_operator_signContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#arithmetic_atomic_value}.
	 * @param ctx the parse tree
	 */
	void enterArithmetic_atomic_value(CTLParser.Arithmetic_atomic_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#arithmetic_atomic_value}.
	 * @param ctx the parse tree
	 */
	void exitArithmetic_atomic_value(CTLParser.Arithmetic_atomic_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#arithmetic_atom}.
	 * @param ctx the parse tree
	 */
	void enterArithmetic_atom(CTLParser.Arithmetic_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#arithmetic_atom}.
	 * @param ctx the parse tree
	 */
	void exitArithmetic_atom(CTLParser.Arithmetic_atomContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#arithmetic_expression3}.
	 * @param ctx the parse tree
	 */
	void enterArithmetic_expression3(CTLParser.Arithmetic_expression3Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#arithmetic_expression3}.
	 * @param ctx the parse tree
	 */
	void exitArithmetic_expression3(CTLParser.Arithmetic_expression3Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#arithmetic_expression2}.
	 * @param ctx the parse tree
	 */
	void enterArithmetic_expression2(CTLParser.Arithmetic_expression2Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#arithmetic_expression2}.
	 * @param ctx the parse tree
	 */
	void exitArithmetic_expression2(CTLParser.Arithmetic_expression2Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#arithmetic_expression1}.
	 * @param ctx the parse tree
	 */
	void enterArithmetic_expression1(CTLParser.Arithmetic_expression1Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#arithmetic_expression1}.
	 * @param ctx the parse tree
	 */
	void exitArithmetic_expression1(CTLParser.Arithmetic_expression1Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#comparison_expression}.
	 * @param ctx the parse tree
	 */
	void enterComparison_expression(CTLParser.Comparison_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#comparison_expression}.
	 * @param ctx the parse tree
	 */
	void exitComparison_expression(CTLParser.Comparison_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#and_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void enterAnd_arithmetic_expression(CTLParser.And_arithmetic_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#and_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void exitAnd_arithmetic_expression(CTLParser.And_arithmetic_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#or_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void enterOr_arithmetic_expression(CTLParser.Or_arithmetic_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#or_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void exitOr_arithmetic_expression(CTLParser.Or_arithmetic_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#ternary_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void enterTernary_arithmetic_expression(CTLParser.Ternary_arithmetic_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#ternary_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void exitTernary_arithmetic_expression(CTLParser.Ternary_arithmetic_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#eq_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void enterEq_arithmetic_expression(CTLParser.Eq_arithmetic_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#eq_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void exitEq_arithmetic_expression(CTLParser.Eq_arithmetic_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#implies_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void enterImplies_arithmetic_expression(CTLParser.Implies_arithmetic_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#implies_arithmetic_expression}.
	 * @param ctx the parse tree
	 */
	void exitImplies_arithmetic_expression(CTLParser.Implies_arithmetic_expressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#proposition}.
	 * @param ctx the parse tree
	 */
	void enterProposition(CTLParser.PropositionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#proposition}.
	 * @param ctx the parse tree
	 */
	void exitProposition(CTLParser.PropositionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(CTLParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(CTLParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#unary_operator}.
	 * @param ctx the parse tree
	 */
	void enterUnary_operator(CTLParser.Unary_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#unary_operator}.
	 * @param ctx the parse tree
	 */
	void exitUnary_operator(CTLParser.Unary_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator5}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator5(CTLParser.Binary_operator5Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator5}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator5(CTLParser.Binary_operator5Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator4}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator4(CTLParser.Binary_operator4Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator4}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator4(CTLParser.Binary_operator4Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator3}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator3(CTLParser.Binary_operator3Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator3}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator3(CTLParser.Binary_operator3Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator2}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator2(CTLParser.Binary_operator2Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator2}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator2(CTLParser.Binary_operator2Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#binary_operator1}.
	 * @param ctx the parse tree
	 */
	void enterBinary_operator1(CTLParser.Binary_operator1Context ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#binary_operator1}.
	 * @param ctx the parse tree
	 */
	void exitBinary_operator1(CTLParser.Binary_operator1Context ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterFormula(CTLParser.FormulaContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitFormula(CTLParser.FormulaContext ctx);
	/**
	 * Enter a parse tree produced by {@link CTLParser#formula_eof}.
	 * @param ctx the parse tree
	 */
	void enterFormula_eof(CTLParser.Formula_eofContext ctx);
	/**
	 * Exit a parse tree produced by {@link CTLParser#formula_eof}.
	 * @param ctx the parse tree
	 */
	void exitFormula_eof(CTLParser.Formula_eofContext ctx);
}